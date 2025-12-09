package dandastino.chess.piece;

public class Piece {
    private final PieceType type;   // KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN
    private final Color color;      // WHITE or BLACK

    public Piece(PieceType type, Color color) {
        this.type = type;
        this.color = color;
    }

    public PieceType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

}
