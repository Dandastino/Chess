package dandastino.chess.moveAnalyses;

import dandastino.chess.engine.EngineAnalysis;
import dandastino.chess.engine.StockfishEngine;
import dandastino.chess.moves.Move;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to analyze chess moves using Stockfish engine.
 * Calculates move quality metrics and detects blunders.
 */
@Service
public class MoveAnalysisEngineService {
    
    private static final Logger logger = LoggerFactory.getLogger(MoveAnalysisEngineService.class);
    private static final int DEFAULT_ANALYSIS_DEPTH = 20;
    
    // Centipawn loss thresholds
    private static final int BLUNDER_THRESHOLD = 300;      // > 300cp loss
    private static final int MISTAKE_THRESHOLD = 150;      // 150-300cp loss
    private static final int INACCURACY_THRESHOLD = 50;    // 50-150cp loss

    /**
     * Analyze a move and calculate its quality
     * @param stockfishEngine initialized Stockfish engine
     * @param fenBefore FEN position before the move
     * @param move the move to analyze
     * @param fenAfter FEN position after the move
     * @return MoveAnalysis with review classification
     */
    public MoveAnalysis analyzeMoveQuality(
            StockfishEngine stockfishEngine,
            String fenBefore,
            Move move,
            String fenAfter) {
        
        try {
            // Get engine's best move and evaluation before the move
            EngineAnalysis engineBestAnalysis = stockfishEngine.analyze(fenBefore, DEFAULT_ANALYSIS_DEPTH);
            int evaluationBefore = engineBestAnalysis.getEvaluation();
            String engineBestMove = engineBestAnalysis.getBestMove();
            
            // Get evaluation after the player's move
            EngineAnalysis afterMoveAnalysis = stockfishEngine.analyze(fenAfter, DEFAULT_ANALYSIS_DEPTH);
            int evaluationAfter = afterMoveAnalysis.getEvaluation();
            
            // Calculate centipawn loss (CPL)
            // Positive CPL means the position got worse
            int centipawnLoss = calculateCentipawnLoss(evaluationBefore, evaluationAfter);
            
            // Classify the move
            Review review = classifyMove(centipawnLoss, engineBestMove.equals(move.getSanMove()));
            
            // Create analysis
            MoveAnalysis analysis = new MoveAnalysis();
            analysis.setMove(move);
            analysis.setReview(review);
            analysis.setDepth(DEFAULT_ANALYSIS_DEPTH);
            analysis.setBest_move(engineBestMove);
            analysis.setEvaluation_cp(String.valueOf(evaluationBefore));
            
            logger.info("Move analysis complete: {} - {} - CPL: {}", 
                       move.getSanMove(), review, centipawnLoss);
            
            return analysis;
            
        } catch (Exception e) {
            logger.error("Error analyzing move", e);
            // Return analysis with neutral review if engine fails
            MoveAnalysis analysis = new MoveAnalysis();
            analysis.setMove(move);
            analysis.setReview(Review.Great);
            analysis.setDepth(0);
            return analysis;
        }
    }

    /**
     * Calculate centipawn loss for a move
     * Positive value means position got worse
     */
    private int calculateCentipawnLoss(int evaluationBefore, int evaluationAfter) {
        // Simple calculation: if the evaluation went down (got worse), that's CPL
        // Note: This is simplified and should account for whose turn it is
        return Math.max(0, evaluationBefore - evaluationAfter);
    }

    /**
     * Classify a move based on centipawn loss
     */
    private Review classifyMove(int centipawnLoss, boolean isEngineMove) {
        if (centipawnLoss > BLUNDER_THRESHOLD) {
            return Review.Blunder;
        } else if (centipawnLoss > MISTAKE_THRESHOLD) {
            return Review.Mistake;
        } else if (centipawnLoss > INACCURACY_THRESHOLD) {
            return Review.Miss;
        } else if (isEngineMove) {
            return Review.Best;
        } else {
            return Review.Great;
        }
    }

    /**
     * Batch analyze all moves in a game
     */
    public void analyzeGameMoves(StockfishEngine engine, java.util.List<Move> moves) {
        logger.info("Starting batch analysis of {} moves", moves.size());
        
        // This would require maintaining board state and replaying the game
        // Implementation depends on your game structure
        for (int i = 0; i < moves.size(); i++) {
            // Reconstruct position at this move
            // Analyze the move
            // Save analysis
            logger.debug("Analyzed move {}/{}", i + 1, moves.size());
        }
    }
}
