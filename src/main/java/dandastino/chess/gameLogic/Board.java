package dandastino.chess.gameLogic;

public class Board {
    private final char[][] squares = new char[8][8];
    // 'r','n','b','q','k','p' (black lower)
    // 'R','N','B','Q','K','P' (white upper)
    // '.' = empty

    private boolean whiteToMove;

    private boolean whiteKingSideCastle;
    private boolean whiteQueenSideCastle;
    private boolean blackKingSideCastle;
    private boolean blackQueenSideCastle;

    // En passant square (e.g. "e3" or null)
    private String enPassantSquare;

    // 50-move rule counters
    private int halfMoveClock;
    private int fullMoveNumber;

    public Board() {
    }

    public static Board fromFen(String fen){
        return FenParser.parse(fen);
    }

    public String toFen(){
        return FenParser.toFen(this);
    }

    public char getPiece(int row, int col){
        return squares[row][col];
    }

    public void setPiece(int row, int col, char piece){}

    public void MovePiece(int fromRow, int toRow, int fromCol, int toCol){
        char piece = squares[fromRow][fromCol];
        squares[fromRow][fromCol] = '.';
        squares[toRow][toCol] = piece;
    }

    public Board copy(){
        Board copy = new Board();
        return copy;
    }
}
