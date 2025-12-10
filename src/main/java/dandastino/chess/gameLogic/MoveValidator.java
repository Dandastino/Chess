package dandastino.chess.gameLogic;

import dandastino.chess.moves.Move;
import dandastino.chess.piece.BoardUtils;
import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceType;

public class MoveValidator {

    public boolean isMoveLegal(Board board, Move move) {
        // 1. Basic checks
        Piece piece = board.getPieceAt(move.getStartRow(), move.getStartCol());
        if (piece == null || !piece.getColor().equals(board.isWhiteToMove() ? Color.WHITE : Color.BLACK)) {
            return false; // No piece, or wrong color attempting to move
        }

        // 2. Geometric/Movement Check (These are the methods below)

        // 3. FINAL King Safety Check must be done externally by the ChessEngine
        return switch (piece.getType()) {
            case PAWN -> validatePawnMove(board, move);
            case KNIGHT -> validateKnightMove(board, move);
            case BISHOP -> validateBishopMove(board, move);
            case ROOK -> validateRookMove(board, move);
            case QUEEN -> validateQueenMove(board, move);
            case KING -> validateKingMove(board, move);
        };
    }

    private boolean isPathClear(Board board, int startRow, int startCol, int endRow, int endCol) {
        int rowStep = Integer.signum(endRow - startRow);
        int colStep = Integer.signum(endCol - startCol);
        int currentRow = startRow + rowStep;
        int currentCol = startCol + colStep;

        while (currentRow != endRow || currentCol != endCol) {
            if (board.getPieceAt(currentRow, currentCol) != null) {
                return true; // Path is BLOCKED
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return false; // Path is CLEAR
    }

    private boolean isDestinationValid(Board board, Move move, Piece movingPiece){
        Piece targetPiece = board.getPieceAt(move.getEndRow(), move.getEndCol());
        // is valid if the square is Empty or contains an opponent's piece
        return targetPiece == null || targetPiece.getColor() != movingPiece.getColor();
    }

    private boolean validatePawnMove(Board board, Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        Piece pawn = board.getPieceAt(startRow, startCol);

        int direction = pawn.getColor() == Color.WHITE ? -1 : 1;
        int rowDifference = endRow - startRow;
        int colDifference = Math.abs(endCol - startCol);

        // Fail fast if not moving forward
        if (rowDifference * direction > 0) return false;

        Piece targetPiece = board.getPieceAt(endRow, endCol);

        // 1. Forward Moves
        if (colDifference == 0) {
            if (Math.abs(rowDifference) == 1) {
                return targetPiece == null;
            } else if (Math.abs(rowDifference) == 2) {
                if (pawn.isInitialMove() && targetPiece == null) {
                    int intermediateRow = startRow + direction;
                    return board.getPieceAt(intermediateRow, startCol) == null; // Must be empty
                }
                return false;
            }
            return false;
        }

        // 2. Capture Moves
        if (colDifference == 1 && Math.abs(rowDifference) == 1) {
            // Standard Capture
            if (targetPiece != null && !targetPiece.getColor().equals(pawn.getColor())) {
                return true;
            }

            // En Passant Capture
            String targetSquareNotation = BoardUtils.toChessNotation(endRow, endCol);
            return targetPiece == null
                    && targetSquareNotation.equals(board.getEnPassantSquare())
                    && rowDifference == direction; // Must be exactly one step in correct direction
        }
        return false;
    }

    private boolean validateKnightMove(Board board, Move move) {
        int rowDiff = Math.abs(move.getEndRow() - move.getStartRow());
        int colDiff = Math.abs(move.getEndCol() - move.getStartCol());

        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) return false;

        Piece movingPiece = board.getPieceAt(move.getStartRow(), move.getStartCol());
        return isDestinationValid(board, move, movingPiece);
    }

    private boolean validateBishopMove(Board board, Move move) {
        int rowDiff = Math.abs(move.getEndRow() - move.getStartRow());
        int colDiff = Math.abs(move.getEndCol() - move.getStartCol());

        if (rowDiff != colDiff || rowDiff == 0) return false; // Not diagonal

        // Fix: Negate isPathClear return value
        if (isPathClear(board, move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol())) {
            return false; // Path is blocked
        }

        Piece movingPiece = board.getPieceAt(move.getStartRow(), move.getStartCol());
        return isDestinationValid(board, move, movingPiece);
    }

    private boolean validateRookMove(Board board, Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        if (!((rowDiff > 0 && colDiff == 0) || (rowDiff == 0 && colDiff > 0))) return false; // Not straight

        // Fix: Negate isPathClear return value
        if (isPathClear(board, startRow, startCol, endRow, endCol)) {
            return false; // Path is blocked
        }

        Piece movingPiece = board.getPieceAt(startRow, startCol);
        return isDestinationValid(board, move, movingPiece);
    }

    private boolean validateQueenMove(Board board, Move move) {
        int rowDiff = Math.abs(move.getEndRow() - move.getStartRow());
        int colDiff = Math.abs(move.getEndCol() - move.getStartCol());

        boolean isDiagonal = rowDiff == colDiff && rowDiff > 0;
        boolean isStraight = (rowDiff > 0 && colDiff == 0) || (rowDiff == 0 && colDiff > 0);

        if (!isDiagonal && !isStraight) return false;

        // Fix: Negate isPathClear return value
        if (isPathClear(board, move.getStartRow(), move.getStartCol(), move.getEndRow(), move.getEndCol())) {
            return false; // Path is blocked
        }

        Piece movingPiece = board.getPieceAt(move.getStartRow(), move.getStartCol());
        return isDestinationValid(board, move, movingPiece);
    }

    private boolean validateKingMove(Board board, Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        Piece king = board.getPieceAt(startRow, startCol);

        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);

        // Standard King Move (1 square)
        if (rowDiff <= 1 && colDiff <= 1 && (rowDiff != 0 || colDiff != 0)) {
            return isDestinationValid(board, move, king);
        }

        // Castling Move (2 squares horizontally)
        if (rowDiff == 0 && colDiff == 2) {
            return validateCastling(board, move, king);
        }

        return false;
    }

    private boolean validateCastling(Board board, Move move, Piece king) {
        if (king.hasMoved()) return false;

        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endCol = move.getEndCol();

        int rookCol = (endCol > startCol) ? 7 : 0;

        Piece rook = board.getPieceAt(startRow, rookCol);
        if (rook == null || !rook.getType().equals(PieceType.ROOK) || rook.hasMoved()) {
            return false;
        }

        // Check Castling Rights (FEN flags)
        String rights = board.getCastlingRights();
        if (king.getColor().equals(Color.WHITE)) {
            if (rookCol == 7 && !rights.contains("K")) return false;
            if (rookCol == 0 && !rights.contains("Q")) return false;
        } else {
            if (rookCol == 7 && !rights.contains("k")) return false;
            if (rookCol == 0 && !rights.contains("q")) return false;
        }

        // Check path clearance
        if (isPathClear(board, startRow, startCol, startRow, rookCol)) {
            return false;
        }

        // Check if squares passed through are attacked
        int step = Integer.signum(endCol - startCol);
        Color opponentColor = king.getColor().equals(Color.WHITE) ? Color.BLACK : Color.WHITE;

        for (int c = startCol; c != endCol + step; c += step) {
            if (isSquareAttacked(board, startRow, c, opponentColor)) {
                return false; // King cannot move through or into check
            }
        }
        return true;
    }

    private boolean isSquareAttacked(Board board, int targetRow, int targetCol, Color attackingColor) {
        for (int r = 0; r < 8; r++){
            for (int c = 0; c < 8; c++){
                Piece attacker = board.getPieceAt(r, c);

                if (attacker != null && attacker.getColor().equals(attackingColor)){
                    if (isValidAttackMovement(board, r, c, targetRow, targetCol, attacker.getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isValidAttackMovement(Board board, int startRow, int startCol, int endRow, int endCol, PieceType type) {
        Move move = new Move(startRow, startCol, endRow, endCol);

        Piece attacker = board.getPieceAt(startRow, startCol);
        if (!isDestinationValid(board, move, attacker)) {
            return false;
        }

        // Note: The move validation functions (except pawn) are already pure geometric checks.
        return switch (type) {
            case PAWN -> validatePawnAttack(board, move);
            case KNIGHT -> validateKnightMove(board, move);
            case BISHOP -> validateBishopMove(board, move);
            case ROOK -> validateRookMove(board, move);
            case QUEEN -> validateQueenMove(board, move);
            case KING -> validateKingMove(board, move);
            default -> false;
        };
    }

    private boolean validatePawnAttack(Board board, Move move){
        int direction = board.getPieceAt(move.getStartRow(), move.getStartCol()).getColor() == Color.WHITE ? -1 : 1;
        int rowDifference = move.getEndRow() - move.getStartRow();
        int colDifference = Math.abs(move.getEndCol() - move.getStartCol());

        return colDifference == 1 && rowDifference == direction;
    }
}