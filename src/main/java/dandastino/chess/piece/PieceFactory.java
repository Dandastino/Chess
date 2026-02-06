package dandastino.chess.piece;

public class PieceFactory {

    /**
     * Create a piece from a FEN character, inferring whether it has moved
     * based on its starting rank/file and the castling rights string.
     */
    public static Piece fromFenChar(char c, int row, int col, String castlingRights) {
        Color color = Character.isUpperCase(c) ? Color.WHITE : Color.BLACK;
        char lowerChar = Character.toLowerCase(c);

        PieceType type = switch (lowerChar) {
            case 'p' -> PieceType.PAWN;
            case 'n' -> PieceType.KNIGHT;
            case 'b' -> PieceType.BISHOP;
            case 'r' -> PieceType.ROOK;
            case 'q' -> PieceType.QUEEN;
            case 'k' -> PieceType.KING;
            default -> throw new IllegalArgumentException("Invalid FEN character: " + c);
        };

        boolean hasMoved = true;

        // Pawns: allow double push only if still on starting rank.
        if (type == PieceType.PAWN) {
            if (color == Color.WHITE && row == 6) hasMoved = false; // white start rank
            if (color == Color.BLACK && row == 1) hasMoved = false; // black start rank
        }

        // Kings/Rooks: infer from castling rights and starting squares.
        if (type == PieceType.KING) {
            if (color == Color.WHITE && (castlingRights.contains("K") || castlingRights.contains("Q"))) {
                hasMoved = false;
            }
            if (color == Color.BLACK && (castlingRights.contains("k") || castlingRights.contains("q"))) {
                hasMoved = false;
            }
        }
        if (type == PieceType.ROOK) {
            if (color == Color.WHITE && row == 7) {
                if (col == 0 && castlingRights.contains("Q")) hasMoved = false; // queenside rook
                if (col == 7 && castlingRights.contains("K")) hasMoved = false; // kingside rook
            }
            if (color == Color.BLACK && row == 0) {
                if (col == 0 && castlingRights.contains("q")) hasMoved = false;
                if (col == 7 && castlingRights.contains("k")) hasMoved = false;
            }
        }

        return new Piece(type, color, hasMoved);
    }

    /**
     * Backward-compatible overload (assumes piece has moved).
     */
    public static Piece fromFenChar(char c) {
        return fromFenChar(c, -1, -1, "-");
    }
}
