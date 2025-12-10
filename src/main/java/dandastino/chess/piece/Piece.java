package dandastino.chess.piece;

public class Piece implements Cloneable {
    private final PieceType type;
    private final Color color;
    private boolean hasMoved;

    public Piece(PieceType type, Color color, boolean hasMoved) {
        this.type = type;
        this.color = color;
        this.hasMoved= hasMoved;
    }

    public PieceType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isInitialMove() {
        return !hasMoved;
    }

    @Override
    public Piece clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError("Piece class should be cloneable", e);
        }
    }

    public char getFenChar() {
        // Logic to determine the character based on type and color
        char fenChar = switch (this.type) {
            case PAWN -> 'p';
            case KNIGHT -> 'n';
            case BISHOP -> 'b';
            case ROOK -> 'r';
            case QUEEN -> 'q';
            case KING -> 'k';
            default -> ' '; // Should not happen
        };
        return this.color == Color.WHITE ? Character.toUpperCase(fenChar) : fenChar;
    }
}
