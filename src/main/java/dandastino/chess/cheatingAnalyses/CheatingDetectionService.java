package dandastino.chess.cheatingAnalyses;

import dandastino.chess.games.Game;
import dandastino.chess.games.GamesRepository;
import dandastino.chess.moves.Move;
import dandastino.chess.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service to detect suspicious playing patterns that indicate potential cheating.
 * Uses multiple heuristics to calculate a suspicion score.
 */
@Service
public class CheatingDetectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(CheatingDetectionService.class);
    
    @Autowired
    private CheatingAnalysesRepository cheatingAnalysesRepository;
    
    @Autowired
    private GamesRepository gamesRepository;

    /**
     * Analyze a completed game for cheating indicators
     */
    public CheatingAnalysis analyzeGame(Game game, List<Move> moves) {
        // Skip analysis for bot games - bots cannot cheat
        if (game.getIsBotGame()) {
            logger.info("Skipping cheating analysis for bot game {}", game.getGame_id());
            // Return null or create a dummy non-suspicious analysis
            return createCheatingAnalysis(game, 0.0, 0.0);
        }
        
        // Check if any player is a bot - skip analysis for human vs bot games
        if ((game.getWhitePlayer() != null && game.getWhitePlayer().getType().equals(dandastino.chess.users.UserType.BOT)) ||
            (game.getBlackPlayer() != null && game.getBlackPlayer().getType().equals(dandastino.chess.users.UserType.BOT))) {
            logger.info("Skipping cheating analysis for game {} - contains bot player", game.getGame_id());
            return createCheatingAnalysis(game, 0.0, 0.0);
        }
        
        // CRITICAL: Check if analysis already exists BEFORE any processing
        List<CheatingAnalysis> existingAnalyses = cheatingAnalysesRepository.findByGameId(game.getGame_id());
        if (!existingAnalyses.isEmpty()) {
            logger.info("Cheating analysis already exists for game {}", game.getGame_id());
            return existingAnalyses.get(0); // Return existing analysis, never create duplicate
        }

        double suspicionScore = 0.0;
        double matchAccuracy = 0.0;

        try {
            if (moves.isEmpty()) {
                logger.info("No moves found for game {}, returning zero score analysis", game.getGame_id());
                return createCheatingAnalysis(game, suspicionScore, matchAccuracy);
            }

            // Calculate individual metrics
            double engineAccuracy = calculateEngineAccuracy(moves);
            double timingAnomaly = calculateTimingAnomaly(moves);
            double skillSpike = calculateSkillSpike(game);
            double moveComplexity = calculateMoveComplexity(moves);

            // Weighted scoring
            suspicionScore = (engineAccuracy * 0.4) +
                           (timingAnomaly * 0.25) +
                           (skillSpike * 0.2) +
                           (moveComplexity * 0.15);

            matchAccuracy = engineAccuracy;

            logger.info("Game {} analysis: score={}, accuracy={}", 
                       game.getGame_id(), suspicionScore, matchAccuracy);

            // Double-check before saving (race condition protection)
            List<CheatingAnalysis> raceCheckAnalyses = cheatingAnalysesRepository.findByGameId(game.getGame_id());
            if (!raceCheckAnalyses.isEmpty()) {
                logger.info("Race condition detected: analysis created while processing. Returning existing analysis.");
                return raceCheckAnalyses.get(0);
            }

            return createCheatingAnalysis(game, suspicionScore, matchAccuracy);

        } catch (Exception e) {
            logger.error("Error analyzing game {} for cheating: {}", game.getGame_id(), e.getMessage(), e);
            // NEVER save anything in the catch block to prevent cascading duplicates
            // Return null or minimal object to signal error, but don't crash the app
            return null;
        }
    }

    /**
     * Calculate what percentage of moves match engine's best moves
     */
    private double calculateEngineAccuracy(List<Move> moves) {
        if (moves.isEmpty()) return 0.0;

        int engineMatches = 0;
        
        for (Move move : moves) {
            // Check if move analysis exists and matches best move
            if (move.getMoveAnalysis() != null) {
                String bestMove = move.getMoveAnalysis().getBest_move();
                if (bestMove != null && bestMove.equals(move.getSanMove())) {
                    engineMatches++;
                }
            }
        }

        return (double) engineMatches / moves.size();
    }

    /**
     * Detect suspicious timing patterns
     * - Moves played too quickly (< 2 seconds for complex positions)
     * - Consistent timing indicating computer-like behavior
     */
    private double calculateTimingAnomaly(List<Move> moves) {
        if (moves.isEmpty()) return 0.0;

        int suspiciousTimings = 0;
        List<Integer> timings = new ArrayList<>();
        
        for (Move move : moves) {
            int timeSpentMs = move.getTimeSpentMs();
            timings.add(timeSpentMs);

            // Flag moves played too quickly in middle/endgame (< 3 seconds)
            // Opening moves can be fast (theory), so skip first 10 moves
            if (move.getMoveNumber() > 10 && timeSpentMs < 3000) {
                suspiciousTimings++;
            }
        }

        // Calculate standard deviation to detect unnatural consistency
        double avgTime = timings.stream().mapToInt(Integer::intValue).average().orElse(0);
        double variance = timings.stream()
                .mapToDouble(t -> Math.pow(t - avgTime, 2))
                .average()
                .orElse(0);
        double stdDev = Math.sqrt(variance);
        
        // Low standard deviation = too consistent = suspicious
        double consistencyScore = stdDev < 2000 ? 0.5 : 0.0;
        
        double quickMoveScore = suspiciousTimings / (double) Math.max(1, moves.size() - 10);
        
        return Math.min(1.0, (quickMoveScore * 0.6) + (consistencyScore * 0.4));
    }

    /**
     * Detect sudden skill improvements
     * Compares recent games to player's historical average
     */
    private double calculateSkillSpike(Game game) {
        try {
            User player = game.getWhitePlayer();
            if (player == null) return 0.0;
            
            // Get player's recent 10 games before this one
            List<Game> recentGames = gamesRepository.findAll().stream()
                    .filter(g -> g.getFinishedAt() != null)
                    .filter(g -> g.getFinishedAt().isBefore(game.getCreatedAt()))
                    .filter(g -> (g.getWhitePlayer() != null && g.getWhitePlayer().getId().equals(player.getId())) ||
                                 (g.getBlackPlayer() != null && g.getBlackPlayer().getId().equals(player.getId())))
                    .sorted(Comparator.comparing(Game::getFinishedAt).reversed())
                    .limit(10)
                    .toList();
            
            if (recentGames.size() < 5) {
                // Not enough history to detect spike
                return 0.0;
            }
            
            // Count wins in recent games
            long recentWins = recentGames.stream()
                    .filter(g -> g.getWinner() != null && g.getWinner().getId().equals(player.getId()))
                    .count();
            
            double recentWinRate = recentWins / (double) recentGames.size();
            
            // Get older games (11-20)
            List<Game> olderGames = gamesRepository.findAll().stream()
                    .filter(g -> g.getFinishedAt() != null)
                    .filter(g -> g.getFinishedAt().isBefore(game.getCreatedAt()))
                    .filter(g -> (g.getWhitePlayer() != null && g.getWhitePlayer().getId().equals(player.getId())) ||
                                 (g.getBlackPlayer() != null && g.getBlackPlayer().getId().equals(player.getId())))
                    .sorted(Comparator.comparing(Game::getFinishedAt).reversed())
                    .skip(10)
                    .limit(10)
                    .toList();
            
            if (olderGames.size() < 5) {
                return 0.0;
            }
            
            long olderWins = olderGames.stream()
                    .filter(g -> g.getWinner() != null && g.getWinner().getId().equals(player.getId()))
                    .count();
            
            double olderWinRate = olderWins / (double) olderGames.size();
            
            // Calculate improvement
            double improvement = recentWinRate - olderWinRate;
            
            // Sudden improvement > 30% is suspicious
            if (improvement > 0.3) {
                return Math.min(1.0, improvement * 1.5);
            }
            
            return 0.0;
            
        } catch (Exception e) {
            logger.error("Error calculating skill spike", e);
            return 0.0;
        }
    }

    /**
     * Calculate move complexity based on game state
     * Engine matches on simple positions are less suspicious
     */
    private double calculateMoveComplexity(List<Move> moves) {
        if (moves.isEmpty()) return 0.0;

        int complexMatchCount = 0;
        int totalComplexMoves = 0;
        
        for (int i = 10; i < moves.size(); i++) { // Skip opening (first 10 moves)
            Move move = moves.get(i);
            totalComplexMoves++;
            
            // Check if this complex move matches engine's best move
            if (move.getMoveAnalysis() != null) {
                String bestMove = move.getMoveAnalysis().getBest_move();
                if (bestMove != null && bestMove.equals(move.getSanMove())) {
                    // In complex positions (middle/endgame), matching engine is suspicious
                    complexMatchCount++;
                }
            }
        }
        
        if (totalComplexMoves == 0) return 0.0;
        
        // High percentage of engine matches in complex positions is suspicious
        double complexMatchRate = complexMatchCount / (double) totalComplexMoves;
        
        return Math.min(1.0, complexMatchRate * 1.2);
    }

    /**
     * Create a CheatingAnalysis entity
     */
    private CheatingAnalysis createCheatingAnalysis(Game game, double suspicionScore, double matchAccuracy) {
        CheatingAnalysis analysis = new CheatingAnalysis();
        analysis.setCheating_analysis_id(UUID.randomUUID()); // Manually generate UUID
        analysis.setCheating_game(game);
        analysis.setCheating_user(game.getWhitePlayer()); // Could analyze both players
        analysis.setSuspicion_score(suspicionScore);
        analysis.setMatch_accuracy_perc(matchAccuracy);
        analysis.setCreated_at(LocalDateTime.now());

        // Save to database
        return cheatingAnalysesRepository.save(analysis);
    }

    /**
     * Get risk level based on suspicion score
     */
    public CheatingRiskLevel getRiskLevel(double suspicionScore) {
        if (suspicionScore > 0.8) {
            return CheatingRiskLevel.VERY_HIGH;
        } else if (suspicionScore > 0.6) {
            return CheatingRiskLevel.HIGH;
        } else if (suspicionScore > 0.4) {
            return CheatingRiskLevel.MEDIUM;
        } else if (suspicionScore > 0.2) {
            return CheatingRiskLevel.LOW;
        } else {
            return CheatingRiskLevel.VERY_LOW;
        }
    }

    /**
     * Generate human-readable cheating report
     */
    public String generateCheatingReport(CheatingAnalysis analysis) {
        CheatingRiskLevel risk = getRiskLevel(analysis.getSuspicion_score());
        
        StringBuilder report = new StringBuilder();
        report.append("=== Cheating Analysis Report ===\n");
        report.append(String.format("Risk Level: %s\n", risk));
        report.append(String.format("Suspicion Score: %.2f / 1.0\n", analysis.getSuspicion_score()));
        report.append(String.format("Engine Match Accuracy: %.1f%%\n", analysis.getMatch_accuracy_perc() * 100));
        report.append(String.format("Analyzed: %s\n", analysis.getCreated_at()));
        
        return report.toString();
    }

    /**
     * Get all suspicious games for a player
     */
    public List<CheatingAnalysis> getSuspiciousGamesForPlayer(UUID playerId, double minSuspicionScore) {
        // Query all analyses for player where suspicion > threshold
        return cheatingAnalysesRepository.findAll().stream()
                .filter(a -> a.getCheating_user().getUser_id().equals(playerId))
                .filter(a -> a.getSuspicion_score() >= minSuspicionScore)
                .sorted(Comparator.comparingDouble(CheatingAnalysis::getSuspicion_score).reversed())
                .toList();
    }
}

/**
 * Enum for cheating risk levels
 */
enum CheatingRiskLevel {
    VERY_LOW("Very Low - No evidence of cheating"),
    LOW("Low - Minor suspicious indicators"),
    MEDIUM("Medium - Some concerning patterns"),
    HIGH("High - Multiple cheating indicators"),
    VERY_HIGH("Very High - Likely cheating detected");

    private final String description;

    CheatingRiskLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
