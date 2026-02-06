package dandastino.chess.engine;

/**
 * Represents the analysis result from Stockfish engine.
 */
public class EngineAnalysis {
    private String bestMove;
    private int evaluation; // in centipawns
    private int depth;

    public EngineAnalysis(String bestMove, int evaluation, int depth) {
        this.bestMove = bestMove;
        this.evaluation = evaluation;
        this.depth = depth;
    }

    public String getBestMove() {
        return bestMove;
    }

    public void setBestMove(String bestMove) {
        this.bestMove = bestMove;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "EngineAnalysis{" +
                "bestMove='" + bestMove + '\'' +
                ", evaluation=" + evaluation +
                ", depth=" + depth +
                '}';
    }
}
