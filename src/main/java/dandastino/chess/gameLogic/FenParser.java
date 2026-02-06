package dandastino.chess.gameLogic;

public class FenParser {

    /**
     * Parses a FEN (Forsyth-Edwards Notation) string and converts it into a Board object.
     *
     * @param fen the FEN string representing the state of a chess game
     * @return a Board object representing the chessboard configuration described by the FEN string
     */
    public static Board parse(String fen) {
        Board board = new Board();
        String[] parts = fen.split(" ");

        Fen.rank(parts, board);
        board.setWhiteToMove(parts[1].equals("w"));
        String castlingRights = parts[2];
        board.setCastlingRights(castlingRights);
        board.setEnPassantSquare(parts[3]);
        board.setHalfMoveClock(Integer.parseInt(parts[4]));
        board.setFullMoveNumber(Integer.parseInt(parts[5]));

        return board;
    }

    /**
     * Converts the state of a chessboard represented by a Board object
     * into its corresponding FEN (Forsyth-Edwards Notation) string.
     *
     * @param board the Board object representing the current state of the chessboard
     * @return a FEN string representation of the given chessboard state
     */
    public static String toFen(Board board) {
        String boardPart = Fen.stringBuilder(board);

        return boardPart +
                " " +
                (board.isWhiteToMove() ? "w" : "b") +
                " " +
                board.getCastlingRights() +
                " " +
                board.getEnPassantSquare() +
                " " +
                board.getHalfMoveClock() +
                " " +
                board.getFullMoveNumber();
    }
}