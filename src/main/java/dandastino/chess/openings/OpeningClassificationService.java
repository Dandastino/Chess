package dandastino.chess.openings;

import dandastino.chess.moves.Move;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Service to classify chess openings using ECO codes.
 * Matches move sequences against known openings database.
 */
@Service
public class OpeningClassificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpeningClassificationService.class);
    
    @Autowired
    private OpeningsRepository openingsRepository;

    /**
     * Classify opening based on the moves played so far
     */
    public Opening classifyOpening(List<Move> movesPlayed) {
        if (movesPlayed.isEmpty()) {
            return null;
        }

        // Convert moves to SAN notation sequence
        String moveSequence = buildMoveSequence(movesPlayed);
        
        logger.debug("Classifying opening from moves: {}", moveSequence);

        // Query database for matching opening
        Opening opening = findMatchingOpening(moveSequence);
        
        if (opening != null) {
            logger.info("Opening classified as: {} ({})", opening.getName(), opening.getEco_code());
        } else {
            logger.debug("No opening classification found for move sequence");
        }

        return opening;
    }

    /**
     * Build SAN move sequence from list of moves
     */
    private String buildMoveSequence(List<Move> moves) {
        StringBuilder sequence = new StringBuilder();
        for (int i = 0; i < Math.min(moves.size(), 10); i++) { // First 10 moves only
            if (i > 0) sequence.append(" ");
            sequence.append(moves.get(i).getSanMove());
        }
        return sequence.toString();
    }

    /**
     * Find matching opening from database
     * Tries to match longest sequence first
     */
    private Opening findMatchingOpening(String moveSequence) {
        // Get all openings from database
        List<Opening> allOpenings = openingsRepository.findAll();

        Opening bestMatch = null;
        int bestMatchLength = 0;

        // Try to match against each opening
        for (Opening opening : allOpenings) {
            if (opening.getMoves() == null) continue;

            String openingMoves = opening.getMoves();
            
            // Check if our move sequence starts with the opening's moves
            if (moveSequence.startsWith(openingMoves)) {
                int matchLength = openingMoves.split(" ").length;
                
                // Keep track of the longest matching opening
                if (matchLength > bestMatchLength) {
                    bestMatch = opening;
                    bestMatchLength = matchLength;
                }
            }
        }

        return bestMatch;
    }

    /**
     * Add a new opening to the database
     */
    public Opening addOpening(String name, String ecoCode, String fenStart, String moves) {
        // Check if opening already exists
        Opening existing = openingsRepository.findByEcoCode(ecoCode)
                .orElse(null);
        
        if (existing != null) {
            logger.warn("Opening with ECO code {} already exists", ecoCode);
            return existing;
        }

        Opening opening = new Opening(name, ecoCode, fenStart, moves);
        Opening saved = openingsRepository.save(opening);
        
        logger.info("Added new opening: {} ({})", name, ecoCode);
        return saved;
    }

    /**
     * Get opening statistics for a player
     */
    public OpeningStatistics getPlayerOpeningStats(UUID playerId) {
        // Query for all games played by this player
        // Analyze which openings they play
        // Return statistics
        
        logger.debug("Calculating opening statistics for player {}", playerId);
        
        return new OpeningStatistics();
    }

    /**
     * Initialize common openings (should be run once)
     * This loads popular ECO codes and opening moves
     */
    public void initializeCommonOpenings() {
        // Italian Game
        addOpening("Italian Game", "C50", 
                  "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                  "e4 e5 Nf3 Nc6 Bc4");

        // Sicilian Defense
        addOpening("Sicilian Defense", "B20",
                  "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                  "e4 c5");

        // French Defense
        addOpening("French Defense", "C00",
                  "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                  "e4 e6");

        // Caro-Kann Defense
        addOpening("Caro-Kann Defense", "B10",
                  "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                  "e4 c6");

        // Ruy Lopez
        addOpening("Ruy Lopez", "C60",
                  "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                  "e4 e5 Nf3 Nc6 Bb5");

        logger.info("Common openings initialized");
    }
}

/**
 * Helper class for opening statistics
 */
class OpeningStatistics {
    private Map<String, Integer> openingFrequency = new HashMap<>();
    private Map<String, Double> openingWinRate = new HashMap<>();

    public Map<String, Integer> getOpeningFrequency() {
        return openingFrequency;
    }

    public Map<String, Double> getOpeningWinRate() {
        return openingWinRate;
    }
}
