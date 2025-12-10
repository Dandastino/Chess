package dandastino.chess.gameLogic;

public class ChessEngine {
    Board board;

    public ChessEngine(String fen) {
        this.board = FenParser.parse(fen);
    }

    public boolean isCheckmate() {}

    public boolean isCheck(){}

    public String generateNewFen(){}

    public boolean isDraw(){}

    public MoveDTO makeMove(String from, String to){}
}
