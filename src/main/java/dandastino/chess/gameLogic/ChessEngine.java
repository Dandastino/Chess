package dandastino.chess.gameLogic;

public class ChessEngine {
    Board board;

    public ChessEngine(String fen) {
        this.board = FenParser.parse(fen);
    }

    public boolean isCheckmate() {}

    public boolean isCheck(){}

    public boolean isIllegalMove(){}

    public String generateNewFen(){}

    public MoveDTO makeMove(String from, String to){}
}
