package dandastino.chess.gameLogic;

import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceFactory; // Assuming this helper is defined

public class FenParser {

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

    public static String toFen(Board board) {
        Fen.stringBuilder(board);

        return " " +
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