package dandastino.chess.engine;

import dandastino.chess.cheatingAnalyses.CheatingAnalysis;
import dandastino.chess.cheatingAnalyses.CheatingAnalysisResponseDTO;
import dandastino.chess.cheatingAnalyses.CheatingDetectionService;
import dandastino.chess.games.Game;
import dandastino.chess.gamesOpenings.GameOpeningDTO;
import dandastino.chess.gamesOpenings.GameOpeningService;
import dandastino.chess.moveAnalyses.MoveAnalysis;
import dandastino.chess.moves.Move;
import dandastino.chess.openings.Opening;
import dandastino.chess.openings.OpeningClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Main orchestrator for AI analysis features (Phase 2).
 * Coordinates Stockfish engine, move analysis, opening classification, and cheating detection.
 */
@Service
public class GameAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(GameAnalysisService.class);
    
    @Value("${stockfish.path:/usr/games/stockfish}")
    private String stockfishPath;
    
    @Autowired
    private OpeningClassificationService openingService;
    
    @Autowired
    private GameOpeningService gameOpeningService;
    
    @Autowired
    private CheatingDetectionService cheatingDetectionService;
    
    private StockfishEngine stockfishEngine;

    /**
     * Initialize Stockfish engine (called on application startup)
     */
    public void initializeEngine() {
        try {
            stockfishEngine = new StockfishEngine(stockfishPath);
            logger.info("Game Analysis Service initialized with Stockfish");
            
            // Initialize common openings database
            openingService.initializeCommonOpenings();
        } catch (Exception e) {
            logger.error("Failed to initialize Stockfish engine", e);
        }
    }

    /**
     * Perform complete analysis on a finished game
     * - Analyze each move
     * - Classify opening
     * - Detect cheating indicators
     */
    public GameAnalysisResult analyzeCompletedGame(Game game, List<Move> moves) {
        if (stockfishEngine == null) {
            logger.warn("Stockfish engine not initialized, skipping analysis");
            return null;
        }

        logger.info("Starting comprehensive analysis of game {}", game.getGame_id());

        GameAnalysisResult result = new GameAnalysisResult();
        result.setGameId(game.getGame_id());

        try {
            // 1. Analyze each move
            List<MoveAnalysis> moveAnalyses = analyzeMoves(moves);
            result.setMoveAnalyses(moveAnalyses);
            logger.info("Analyzed {} moves", moveAnalyses.size());

            // 2. Classify opening
            Opening opening = openingService.classifyOpening(moves);
            result.setOpening(opening);
            if (opening != null) {
                logger.info("Opening: {} ({})", opening.getName(), opening.getEco_code());
                // Save the game-opening relationship to database
                try {
                    GameOpeningDTO gameOpeningDTO = new GameOpeningDTO(game.getGame_id(), opening.getOpening_id());
                    gameOpeningService.createGameOpening(gameOpeningDTO);
                    logger.info("Saved game opening relationship for game {} and opening {}", game.getGame_id(), opening.getOpening_id());
                } catch (Exception e) {
                    logger.error("Failed to save game opening relationship: {}", e.getMessage());
                }
            }

            // 3. Detect cheating - now with proper error handling
            try {
                CheatingAnalysis cheatingAnalysis = cheatingDetectionService.analyzeGame(game, moves);
                if (cheatingAnalysis != null) {
                    // Convert entity to DTO to avoid lazy loading issues
                    CheatingAnalysisResponseDTO dto = new CheatingAnalysisResponseDTO(
                            cheatingAnalysis.getCheating_analysis_id(),
                            cheatingAnalysis.getCheating_game() != null ? cheatingAnalysis.getCheating_game().getGame_id() : null,
                            cheatingAnalysis.getCheating_user() != null ? cheatingAnalysis.getCheating_user().getUser_id() : null,
                            cheatingAnalysis.getMatch_accuracy_perc(),
                            cheatingAnalysis.getSuspicion_score(),
                            cheatingAnalysis.getCreated_at()
                    );
                    result.setCheatingAnalysis(dto);
                    logger.info("Cheating analysis: suspicion={}", cheatingAnalysis.getSuspicion_score());
                } else {
                    logger.warn("Cheating analysis returned null for game {}", game.getGame_id());
                    result.setCheatingAnalysisError("Cheating analysis could not be completed");
                }
            } catch (Exception e) {
                logger.error("Failed to perform cheating analysis for game {}: {}", game.getGame_id(), e.getMessage());
                result.setCheatingAnalysisError("Cheating analysis failed: " + e.getMessage());
            }

            // 4. Generate insights
            GameInsights insights = generateInsights(game, moveAnalyses, opening);
            result.setInsights(insights);

            result.setSuccess(true);
            return result;

        } catch (Exception e) {
            logger.error("Error analyzing game", e);
            result.setSuccess(false);
            result.setError(e.getMessage());
            return result;
        }
    }

    /**
     * Analyze individual moves in a game
     */
    private List<MoveAnalysis> analyzeMoves(List<Move> moves) {
        // In a real implementation, would replay the game and analyze each position
        // For now, this is a placeholder that shows the flow
        
        logger.debug("Analyzing {} moves", moves.size());
        
        // TODO: Implement move-by-move analysis
        // Would need to:
        // 1. Start from initial position
        // 2. For each move: get FEN before, apply move, get FEN after
        // 3. Call moveAnalysisService.analyzeMoveQuality()
        // 4. Save results
        
        return List.of();
    }

    /**
     * Generate personalized insights for the player
     */
    private GameInsights generateInsights(Game game, List<MoveAnalysis> moveAnalyses, Opening opening) {
        GameInsights insights = new GameInsights();

        // Analyze move patterns
        if (!moveAnalyses.isEmpty()) {
            long blunders = moveAnalyses.stream()
                    .filter(m -> m.getReview().toString().equals("blunder"))
                    .count();
            long mistakes = moveAnalyses.stream()
                    .filter(m -> m.getReview().toString().equals("mistake"))
                    .count();

            insights.setBlunderCount((int) blunders);
            insights.setMistakeCount((int) mistakes);
            
            // Generate text insights
            if (blunders > 0) {
                insights.addInsight("You had " + blunders + " blunder(s) in this game. Focus on critical positions!");
            }
            if (mistakes > 0) {
                insights.addInsight("You made " + mistakes + " mistake(s). Review these positions to improve.");
            }
        }

        // Analyze opening
        if (opening != null) {
            insights.setOpeningPlayed(opening.getName());
            insights.addInsight("You played the " + opening.getName() + " (" + opening.getEco_code() + ")");
        }

        return insights;
    }

    /**
     * Shutdown engine gracefully
     */
    public void shutdownEngine() {
        if (stockfishEngine != null) {
            stockfishEngine.quit();
            logger.info("Game Analysis Service shutdown complete");
        }
    }
}

/**
 * Container for complete game analysis results
 */
class GameAnalysisResult {
    private java.util.UUID gameId;
    private List<MoveAnalysis> moveAnalyses;
    private Opening opening;
    private CheatingAnalysisResponseDTO cheatingAnalysis;
    private String cheatingAnalysisError;
    private GameInsights insights;
    private boolean success;
    private String error;

    // Getters and setters
    public java.util.UUID getGameId() { return gameId; }
    public void setGameId(java.util.UUID gameId) { this.gameId = gameId; }

    public List<MoveAnalysis> getMoveAnalyses() { return moveAnalyses; }
    public void setMoveAnalyses(List<MoveAnalysis> moveAnalyses) { this.moveAnalyses = moveAnalyses; }

    public Opening getOpening() { return opening; }
    public void setOpening(Opening opening) { this.opening = opening; }

    public CheatingAnalysisResponseDTO getCheatingAnalysis() { return cheatingAnalysis; }
    public void setCheatingAnalysis(CheatingAnalysisResponseDTO cheatingAnalysis) { this.cheatingAnalysis = cheatingAnalysis; }

    public String getCheatingAnalysisError() { return cheatingAnalysisError; }
    public void setCheatingAnalysisError(String cheatingAnalysisError) { this.cheatingAnalysisError = cheatingAnalysisError; }

    public GameInsights getInsights() { return insights; }
    public void setInsights(GameInsights insights) { this.insights = insights; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}

/**
 * Container for game insights
 */
class GameInsights {
    private String openingPlayed;
    private int blunderCount;
    private int mistakeCount;
    private List<String> insights = new java.util.ArrayList<>();

    public void addInsight(String insight) {
        insights.add(insight);
    }

    // Getters and setters
    public String getOpeningPlayed() { return openingPlayed; }
    public void setOpeningPlayed(String openingPlayed) { this.openingPlayed = openingPlayed; }

    public int getBlunderCount() { return blunderCount; }
    public void setBlunderCount(int blunderCount) { this.blunderCount = blunderCount; }

    public int getMistakeCount() { return mistakeCount; }
    public void setMistakeCount(int mistakeCount) { this.mistakeCount = mistakeCount; }

    public List<String> getInsights() { return insights; }
    public void setInsights(List<String> insights) { this.insights = insights; }
}
