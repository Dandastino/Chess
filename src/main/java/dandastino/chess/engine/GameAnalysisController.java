package dandastino.chess.engine;

import dandastino.chess.exceptions.BadRequestException;
import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.games.Game;
import dandastino.chess.games.GamesRepository;
import dandastino.chess.games.Status;
import dandastino.chess.moves.MovesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * REST API endpoints for game analysis (Phase 2 AI features)
 */
@RestController
@RequestMapping("/api/analysis")
public class GameAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(GameAnalysisController.class);
    
    @Autowired
    private GameAnalysisService gameAnalysisService;
    
    @Autowired
    private GamesRepository gamesRepository;
    
    @Autowired
    private MovesRepository movesRepository;

    /**
     * Analyze a completed game
     * GET /api/analysis/game/{gameId}
     * 
     * Status codes:
     * - 200: Analysis completed successfully
     * - 400: Invalid game state or incomplete game
     * - 404: Game not found
     * - 500: Database constraint violation or internal error
     * - 503: Analysis engine not initialized
     */
    @GetMapping("/game/{gameId}")
    public ResponseEntity<?> analyzeGame(@PathVariable UUID gameId) {
        try {
            logger.info("Analyzing game: {}", gameId);
            
            // Validate gameId
            if (gameId == null) {
                throw new BadRequestException("Game ID cannot be null");
            }
            
            // Find game
            Game game = gamesRepository.findById(gameId)
                    .orElseThrow(() -> new NotFoundException("Game with ID " + gameId + " not found"));
            
            // Validate game is finished
            if (game.getStatus() == null || game.getStatus() != Status.done) {
                throw new BadRequestException("Game must be finished before analysis. Current status: " + game.getStatus());
            }
            
            var moves = movesRepository.findByGameId(gameId);
            
            // Validate moves exist
            if (moves.isEmpty()) {
                throw new BadRequestException("No moves found for game " + gameId);
            }
            
            GameAnalysisResult result = gameAnalysisService.analyzeCompletedGame(game, moves);
            
            if (result == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Analysis engine not initialized. Please call POST /api/analysis/init first.");
            }
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                logger.error("Analysis failed for game {}: {}", gameId, result.getError());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Analysis failed: " + result.getError());
            }
            
        } catch (NotFoundException e) {
            logger.error("Game not found: {}", gameId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (BadRequestException e) {
            logger.error("Bad request for game analysis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            logger.error("Database constraint violation during analysis", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error: Unable to save analysis results. Please check database schema.");
        } catch (Exception e) {
            logger.error("Unexpected error analyzing game {}", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Get move analysis for a specific move
     * GET /api/analysis/move/{moveId}
     * 
     * Status codes:
     * - 200: Move found and returned successfully
     * - 400: Invalid move ID
     * - 404: Move not found
     */
    @GetMapping("/move/{moveId}")
    public ResponseEntity<?> analyzeMoveDetails(@PathVariable UUID moveId) {
        try {
            logger.info("Getting move analysis: {}", moveId);
            
            // Validate moveId
            if (moveId == null) {
                throw new BadRequestException("Move ID cannot be null");
            }
            
            // Retrieve move and its analysis
            var move = movesRepository.findById(moveId)
                    .orElseThrow(() -> new NotFoundException("Move with ID " + moveId + " not found"));
            
            // Return move with analysis details
            return ResponseEntity.ok(move);
            
        } catch (NotFoundException e) {
            logger.error("Move not found: {}", moveId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (BadRequestException e) {
            logger.error("Bad request for move analysis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving move {}", moveId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Initialize the analysis engine
     * POST /api/analysis/init
     * 
     * Status codes:
     * - 200: Engine initialized successfully
     * - 500: Engine initialization failed
     */
    @PostMapping("/init")
    public ResponseEntity<?> initializeEngine() {
        try {
            logger.info("Initializing analysis engine");
            gameAnalysisService.initializeEngine();
            return ResponseEntity.ok("Analysis engine initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing engine", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to initialize engine: " + e.getMessage());
        }
    }

    /**
     * Shutdown the analysis engine
     * POST /api/analysis/shutdown
     * 
     * Status codes:
     * - 200: Engine shutdown successfully
     * - 500: Engine shutdown failed
     */
    @PostMapping("/shutdown")
    public ResponseEntity<?> shutdownEngine() {
        try {
            logger.info("Shutting down analysis engine");
            gameAnalysisService.shutdownEngine();
            return ResponseEntity.ok("Analysis engine shutdown successfully");
        } catch (Exception e) {
            logger.error("Error shutting down engine", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to shutdown engine: " + e.getMessage());
        }
    }

    /**
     * Get game insights
     * GET /api/analysis/insights/{gameId}
     * 
     * Status codes:
     * - 200: Insights retrieved successfully
     * - 204: No insights available (game not analyzed yet)
     * - 400: Invalid game ID
     * - 404: Game not found
     * - 500: Error retrieving insights
     * - 503: Analysis engine not initialized
     */
    @GetMapping("/insights/{gameId}")
    public ResponseEntity<?> getGameInsights(@PathVariable UUID gameId) {
        try {
            logger.info("Fetching insights for game: {}", gameId);
            
            // Validate gameId
            if (gameId == null) {
                throw new BadRequestException("Game ID cannot be null");
            }
            
            Game game = gamesRepository.findById(gameId)
                    .orElseThrow(() -> new NotFoundException("Game with ID " + gameId + " not found"));
            
            var moves = movesRepository.findByGameId(gameId);
            
            // Validate moves exist
            if (moves.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No moves found for game " + gameId);
            }
            
            // For now, return analysis result with insights
            GameAnalysisResult result = gameAnalysisService.analyzeCompletedGame(game, moves);
            
            if (result == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Analysis engine not initialized. Please call POST /api/analysis/init first.");
            }
            
            if (result.isSuccess() && result.getInsights() != null) {
                return ResponseEntity.ok(result.getInsights());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No insights available for game " + gameId);
            }
            
        } catch (NotFoundException e) {
            logger.error("Game not found: {}", gameId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (BadRequestException e) {
            logger.error("Bad request for insights: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error retrieving insights for game {}", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Health check for analysis service
     * GET /api/analysis/health
     * 
     * Status codes:
     * - 200: Service is running
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(new HealthStatus("Analysis service is running"));
    }
}

/**
 * Simple health status response
 */
class HealthStatus {
    private String status;
    private long timestamp;

    public HealthStatus(String status) {
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
}
