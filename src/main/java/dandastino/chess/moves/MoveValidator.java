package dandastino.chess.moves;

import dandastino.chess.gameLogic.Board;
import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;

public class MoveValidator {

    public boolean isMoveLegal(Board board, Move move) {
        Piece piece = board.getPieceAt(move.getStartRow(), move.getStartCol());

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

    private boolean isPathClear(Board board, int startRow, int startCol, int endRow, int endCol) {
        int rowStep = Integer.signum(endRow - startRow); // -1, 0, or 1
        int colStep = Integer.signum(endCol - startCol); // -1, 0, or 1

        int currentRow = startRow + rowStep;
        int currentCol = startCol + colStep;

        while (currentRow != endRow || currentCol != endCol) {
            if (board.getPieceAt(currentRow, currentCol) != null) {
                return false; // Found a piece blocking the path
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true;
    }

    private boolean validatePawnMove(Board board, Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        Piece pawn = board.getPieceAt(startRow, startCol);

        int direction = pawn.getColor() == Color.WHITE ? -1 : 1; // -1 for White (up), 1 for Black (down)
        int rowDifference = endRow - startRow;
        int colDifference = Math.abs(endCol - startCol);

        if (pawn.getColor() == Color.WHITE && rowDifference >= 0) return false;
        if (pawn.getColor() == Color.BLACK && rowDifference <= 0) return false;

        if (rowDifference * direction > 0) return false; // Ensure move is "forward" (e.g., -1 * -1 = +1, which is illegal)
        if (rowDifference * direction < -2) return false; // Ensure move is at most 2 steps

        Piece targetPiece = board.getPieceAt(endRow, endCol);

        if (colDifference == 0){
            if(Math.abs(rowDifference) == 1){
                return targetPiece == null;
            }
        }

        if (colDifference == 0) {
            if (Math.abs(rowDifference) == 1) {
                return targetPiece == null; // Can only move to an empty square
            }

            if (Math.abs(rowDifference) == 2) {
                // Must be the pawn's first move AND the two intervening squares must be empty
                if (pawn.isInitialMove()) {
                    int intermediateRow = startRow + direction;
                    Piece intermediatePiece = board.getPieceAt(intermediateRow, startCol);

                    return intermediatePiece == null && targetPiece == null;
                }
                return false;
            }
        }

        if (colDifference == 1) {
            if (Math.abs(rowDifference) == 1) {
                return targetPiece != null && targetPiece.getColor() != pawn.getColor();
            }
        }

        return false;
    }

    private boolean validateKnightMove(Board board, Move move) { … }
    private boolean validateBishopMove(Board board, Move move) { … }
    private boolean validateRookMove(Board board, Move move) { … }
    private boolean validateQueenMove(Board board, Move move) { … }
    private boolean validateKingMove(Board board, Move move) { … }

}

