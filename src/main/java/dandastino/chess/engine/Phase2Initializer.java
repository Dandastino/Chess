package dandastino.chess.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes Phase 2 AI analysis features on application startup
 */
@Component
public class Phase2Initializer {
    
    private static final Logger logger = LoggerFactory.getLogger(Phase2Initializer.class);
    
    @Autowired
    private GameAnalysisService gameAnalysisService;

    /**
     * Called after application startup to initialize Stockfish engine
     * Running async to prevent blocking startup
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void initializeAnalysisEngine() {
        try {
            logger.info("========== Starting Phase 2 AI Analysis initialization (async)... ==========");
            gameAnalysisService.initializeEngine();
            logger.info("========== Phase 2 AI Analysis Features Initialized Successfully ==========");
            logger.info("Available features:");
            logger.info("  - Stockfish engine integration via UCI");
            logger.info("  - Move analysis with centipawn loss calculation");
            logger.info("  - Opening classification with ECO codes");
            logger.info("  - Cheating detection with suspicion scoring");
            logger.info("  - Player insights generation");
        } catch (Exception e) {
            logger.warn("Phase 2 AI features not available: {}", e.getMessage(), e);
            logger.info("Note: Install Stockfish to enable AI analysis features");
            logger.info("  Linux/Mac: brew install stockfish");
            logger.info("  Windows: Download from https://stockfishchess.org/");
            // DO NOT throw exception - allow app to continue without Stockfish
        }
    }
}
