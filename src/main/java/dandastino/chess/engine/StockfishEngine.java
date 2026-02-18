package dandastino.chess.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface with Stockfish chess engine via UCI protocol.
 * Handles position analysis and best move calculation.
 */
public class StockfishEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(StockfishEngine.class);
    private Process stockfishProcess;
    private BufferedWriter stockfishInput;
    private BufferedReader stockfishOutput;
    private static final int DEFAULT_TIMEOUT_MS = 5000;

    public StockfishEngine(String stockfishPath) throws Exception {
        initializeEngine(stockfishPath);
    }

    /**
     * Initialize the Stockfish engine process
     */
    private void initializeEngine(String stockfishPath) throws Exception {
        try {
            stockfishProcess = new ProcessBuilder(stockfishPath).start();
            stockfishInput = new BufferedWriter(new OutputStreamWriter(stockfishProcess.getOutputStream()));
            stockfishOutput = new BufferedReader(new InputStreamReader(stockfishProcess.getInputStream()));
            
            // Send UCI command and wait for response
            sendCommand("uci");
            waitForResponse("uciok", DEFAULT_TIMEOUT_MS);
            
            logger.info("Stockfish engine initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Stockfish engine", e);
            throw e;
        }
    }

    /**
     * Set the position using FEN notation
     */
    public void setPosition(String fen) throws Exception {
        String command = "position fen " + fen;
        sendCommand(command);
    }

    /**
     * Analyze a position and return the best move
     */
    public EngineAnalysis analyze(String fen, int depth) throws Exception {
        setPosition(fen);
        
        String command = "go depth " + depth;
        sendCommand(command);
        
        EngineAnalysis analysis = parseAnalysis(DEFAULT_TIMEOUT_MS);
        logger.debug("Analysis: bestMove={}, eval={}, depth={}", 
                    analysis.getBestMove(), analysis.getEvaluation(), analysis.getDepth());
        
        return analysis;
    }

    /**
     * Analyze a position with time limit (milliseconds)
     */
    public EngineAnalysis analyzeWithTimeLimit(String fen, int timeLimitMs) throws Exception {
        setPosition(fen);
        
        String command = "go movetime " + timeLimitMs;
        sendCommand(command);
        
        EngineAnalysis analysis = parseAnalysis(timeLimitMs + 1000);
        return analysis;
    }

    /**
     * Parse the "bestmove" response from Stockfish
     */
    private EngineAnalysis parseAnalysis(long timeoutMs) throws Exception {
        long startTime = System.currentTimeMillis();
        String bestMove = null;
        int evaluation = 0;
        int depth = 0;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            String line = stockfishOutput.readLine();
            if (line == null) continue;

            logger.debug("Stockfish output: {}", line);

            // Parse "info depth X ... eval Y ... pv Z"
            if (line.startsWith("info")) {
                Pattern depthPattern = Pattern.compile("depth (\\d+)");
                Matcher depthMatcher = depthPattern.matcher(line);
                if (depthMatcher.find()) {
                    depth = Integer.parseInt(depthMatcher.group(1));
                }

                Pattern evalPattern = Pattern.compile("cp (-?\\d+)|mate (-?\\d+)");
                Matcher evalMatcher = evalPattern.matcher(line);
                if (evalMatcher.find()) {
                    if (evalMatcher.group(1) != null) {
                        evaluation = Integer.parseInt(evalMatcher.group(1));
                    } else if (evalMatcher.group(2) != null) {
                        // Mate in X - convert to large evaluation
                        int mateIn = Integer.parseInt(evalMatcher.group(2));
                        evaluation = mateIn > 0 ? 30000 - (mateIn * 100) : -30000 + (Math.abs(mateIn) * 100);
                    }
                }
            }

            // Parse "bestmove X ponder Y"
            if (line.startsWith("bestmove")) {
                Pattern bestMovePattern = Pattern.compile("bestmove ([a-h][1-8][a-h][1-8][qrbn]?)");
                Matcher bestMoveMatcher = bestMovePattern.matcher(line);
                if (bestMoveMatcher.find()) {
                    bestMove = bestMoveMatcher.group(1);
                    break;
                }
            }
        }

        if (bestMove == null) {
            throw new Exception("Stockfish did not return a best move within timeout");
        }

        return new EngineAnalysis(bestMove, evaluation, depth);
    }

    /**
     * Send a command to Stockfish
     */
    private void sendCommand(String command) throws Exception {
        logger.debug("Sending to Stockfish: {}", command);
        stockfishInput.write(command + "\n");
        stockfishInput.flush();
    }

    /**
     * Wait for a specific response from Stockfish
     */
    private void waitForResponse(String expectedResponse, long timeoutMs) throws Exception {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            String line = stockfishOutput.readLine();
            if (line != null && line.contains(expectedResponse)) {
                return;
            }
        }
        
        throw new Exception("Timeout waiting for response: " + expectedResponse);
    }

    /**
     * Quit the engine gracefully
     */
    public void quit() {
        try {
            if (stockfishProcess != null && stockfishProcess.isAlive()) {
                try {
                    sendCommand("quit");
                    stockfishProcess.waitFor();
                    logger.info("Stockfish engine terminated gracefully");
                } catch (Exception e) {
                    logger.warn("Could not send quit command, forcing termination: {}", e.getMessage());
                    stockfishProcess.destroyForcibly();
                }
            } else {
                logger.info("Stockfish process was already terminated");
            }
        } catch (Exception e) {
            logger.error("Error during Stockfish shutdown", e);
        } finally {
            try {
                if (stockfishInput != null) stockfishInput.close();
                if (stockfishOutput != null) stockfishOutput.close();
            } catch (Exception e) {
                logger.debug("Error closing streams: {}", e.getMessage());
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        quit();
        super.finalize();
    }
}
