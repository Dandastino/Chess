package dandastino.chess.moves;

import dandastino.chess.gameLogic.Board;
import dandastino.chess.piece.Piece;

public class MoveValidator {

    public boolean isMoveLegal(Board board, Move move) {
        Piece piece = board.getPiece();

        if (piece == null) return false;

        return switch (piece.getType()) {
            case PAWN -> validatePawnMove(board, move);
            case KNIGHT -> validateKnightMove(board, move);
            case BISHOP -> validateBishopMove(board, move);
            case ROOK -> validateRookMove(board, move);
            case QUEEN -> validateQueenMove(board, move);
            case KING -> validateKingMove(board, move);
            default -> false;
        };
    }

    private boolean validatePawnMove(Board board, Move move) { }
    private boolean validateKnightMove(Board board, Move move) { }
    private boolean validateBishopMove(Board board, Move move) { }
    private boolean validateRookMove(Board board, Move move) { }
    private boolean validateQueenMove(Board board, Move move) { }
    private boolean validateKingMove(Board board, Move move) { }
}

