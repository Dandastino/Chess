package dandastino.chess.piece;

public class PieceFactory {

    public static Piece fromFenChar(char c) {
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

        // When parsing a FEN string, pieces are NOT in their initial positions,
        // so we must assume they HAVE moved, EXCEPT for the starting FEN position ("rnbqkbnr/...")
        // For simplicity and safety during parsing, initialize 'hasMoved' based on the row.
        // A full implementation might check if the piece is on its starting square.

        // A robust solution is to set 'hasMoved' to TRUE for all pieces,
        // as the FEN string itself already implicitly defines castling rights and move clocks.

        return new Piece(type, color, true); // Initialize with hasMoved=true for safety.
    }
}
