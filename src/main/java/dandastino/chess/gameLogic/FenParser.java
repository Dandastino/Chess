package dandastino.chess.gameLogic;

import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceFactory; // Assuming this helper is defined

public class FenParser {

    /**
     * Parses a FEN string and sets the state of a new Board object.
     * @param fen The FEN string to parse.
     * @return A fully initialized Board object.
     */
    public static Board parse(String fen) {
        Board board = new Board(); // Create the new board instance
        String[] parts = fen.split(" ");

        // 1. Read Piece Placement (Board State)
        String[] ranks = parts[0].split("/");
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : ranks[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    // Skip empty squares
                    col += Character.getNumericValue(c);
                } else {
                    // Create and set the piece object
                    Piece piece = PieceFactory.fromFenChar(c);
                    board.setPiece(row, col++, piece);
                }
            }
        }

        // 2. Set Side to Move (Turn)
        board.setWhiteToMove(parts[1].equals("w"));

        // 3. Set Castling Rights
        String castlingRights = parts[2];
        // Note: You must update Board setters to handle this string
        board.setCastlingRights(castlingRights); // Example setter, you need to implement this

        // 4. Set En Passant Square
        board.setEnPassantSquare(parts[3]);

        // 5. Set Halfmove + Fullmove Counters
        board.setHalfMoveClock(Integer.parseInt(parts[4]));
        board.setFullMoveNumber(Integer.parseInt(parts[5]));

        return board;
    }

    // ---

    /**
     * Generates a FEN string representing the current state of the given Board.
     * @param board The Board object to serialize.
     * @return The FEN string.
     */
    public static String toFen(Board board) {
        StringBuilder sb = new StringBuilder();

        // 1. Board state to FEN
        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col);

                if (piece == null) {
                    empty++; // Increment empty counter
                } else {
                    if (empty > 0) {
                        sb.append(empty); // Add empty count if non-zero
                        empty = 0;
                    }
                    sb.append(piece.getFenChar()); // Add piece character
                }
            }
            if (empty > 0) sb.append(empty); // Add trailing empty count
            if (row < 7) sb.append('/');
        }

        // 2. Append metadata (Side to Move, Castling, En Passant, Clocks)
        sb.append(" ")
                .append(board.isWhiteToMove() ? "w" : "b")
                .append(" ")
                // Note: You need a getter for castling rights string
                .append(board.getCastlingRights())
                .append(" ")
                .append(board.getEnPassantSquare())
                .append(" ")
                .append(board.getHalfMoveClock())
                .append(" ")
                .append(board.getFullMoveNumber());

        return sb.toString();
    }
}