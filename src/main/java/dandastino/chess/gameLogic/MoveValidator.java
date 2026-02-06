package dandastino.chess.gameLogic;

import dandastino.chess.moves.Move;
import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceType;

public class MoveValidator {

    /**
     * Determines whether a given chess move is legal based on the current state of the board
     * and the rules of chess for the specific piece being moved.
     *
     * @param board the current state of the board, which includes the positions of all pieces
     *              and the current turn information
     * @param move  the move to evaluate, including the starting and target positions, and the piece that is being moved
     * @return true if the move is legal according to the rules of chess and the state of the game;
     *         false otherwise
     */
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

    /**
     * Checks if the path between two positions on the board is clear. The method
     * assumes a straight or diagonal path and iterates through each position along
     * the path to check for any obstacles.
     *
     * @param board the board representation containing the current state and positions
     *              of all pieces
     * @param startRow the starting row index of the path
     * @param startCol the starting column index of the path
     * @param endRow the ending row index of the path
     * @param endCol the ending column index of the path
     * @return true if a piece is encountered along the path (indicating the path
     *         is blocked); false if the path is entirely clear
     */
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

    /**
     * Determines whether the destination of a move is valid for the moving piece.
     * A destination is valid if the target square is empty or occupied by an opponent's piece.
     *
     * @param board the current state of the chess board
     * @param move the move being evaluated, including start and end positions
     * @param movingPiece the piece that is being moved
     * @return true if the destination is valid for the move; false otherwise
     */
    private boolean isDestinationValid(Board board, Move move, Piece movingPiece){
        Piece targetPiece = board.getPieceAt(move.getEndRow(), move.getEndCol());
        // is valid if the square is Empty or contains an opponent's piece
        return targetPiece == null || targetPiece.getColor() != movingPiece.getColor();
    }

    /**
     * Validates whether the given move for a pawn is legal according to the rules of chess
     * and the current state of the board. This includes checks for forward moves, capturing
     * moves (including en passant), and special considerations for the pawn's initial move.
     *
     * @param board the current state of the chessboard, including positions of all pieces
     *              and additional game information such as en passant target squares
     * @param move the move being evaluated, including the starting and ending positions
     *             of the pawn
     * @return true if the move is valid for a pawn under the given board state; false otherwise
     */
    boolean validatePawnMove(Board board, Move move) {
        int startRow = move.getStartRow();
        int startCol = move.getStartCol();
        int endRow = move.getEndRow();
        int endCol = move.getEndCol();

        Piece pawn = board.getPieceAt(startRow, startCol);

        int direction = pawn.getColor() == Color.WHITE ? -1 : 1;
        int rowDifference = endRow - startRow;
        int colDifference = Math.abs(endCol - startCol);

        // Fail fast if moving backward relative to the pawn's color
        if (rowDifference * direction < 0) return false;

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

    /**
     * Validates whether a given move for a knight is legal based on the rules of chess.
     * A knight's movement is considered valid if it moves in an "L" shape, i.e.,
     * two squares in one direction and one square perpendicular to that, or
     * one square in one direction and two squares perpendicular to that.
     *
     * @param board the current state of the chessboard, containing the positions of all pieces
     *              and additional game information such as whose turn it is
     * @param move the move being evaluated, including the starting and ending positions
     *             of the knight
     * @return true if the move is valid for a knight under the given board state;
     *         false otherwise
     */
    boolean validateKnightMove(Board board, Move move) {
        int rowDiff = Math.abs(move.getEndRow() - move.getStartRow());
        int colDiff = Math.abs(move.getEndCol() - move.getStartCol());

        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) return false;

        Piece movingPiece = board.getPieceAt(move.getStartRow(), move.getStartCol());
        return isDestinationValid(board, move, movingPiece);
    }

    /**
     * Validates whether a given move for a bishop is legal based on the rules of chess.
     * A bishop's movement is valid if it moves diagonally and the path to the target
     * square is clear.
     *
     * @param board the current state of the chessboard, which includes the positions of all pieces
     *              and whose turn it is
     * @param move the move to evaluate, including the starting and ending positions of the bishop
     * @return true if the move is valid for a bishop under the current board state; false otherwise
     */
    boolean validateBishopMove(Board board, Move move) {
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

    /**
     * Validates whether a given move for a rook is legal based on the rules of chess.
     * A rook's movement is considered valid if it moves in a straight line (either horizontally
     * or vertically) without any obstacles in its path.
     *
     * @param board the current state of the chessboard, which includes the positions of all pieces
     *              and the turn information
     * @param move the move being evaluated, including the starting and ending positions of the rook
     * @return true if the move is valid for a rook under the given board state; false otherwise
     */
    boolean validateRookMove(Board board, Move move) {
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

    /**
     * Validates whether a given move for a queen is legal based on the rules of chess.
     * A queen's movement is considered valid if it moves in a straight line in any direction
     * (horizontally, vertically, or diagonally) and the path to the target square is clear.
     *
     * @param board the current state of the chessboard, including the positions of all pieces
     *              and whose turn it is
     * @param move the move to evaluate, including the starting and ending positions of the queen
     * @return true if the move is valid for a queen under the current board state; false otherwise
     */
    boolean validateQueenMove(Board board, Move move) {
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

    /**
     * Validates whether the given move for a king is legal based on the rules of chess.
     * A king's movement is considered valid if it moves one square in any direction
     * (horizontally, vertically, or diagonally) or performs a valid castling move.
     * This method checks the legality of the movement and considers special rules
     * such as castling.
     *
     * @param board the current state of the chessboard, including the positions of
     *              all pieces and any necessary game state information
     * @param move the move being evaluated, including the starting and ending positions
     *             of the king
     * @return true if the move is valid for a king under the given board state; false otherwise
     */
    boolean validateKingMove(Board board, Move move) {
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

    /**
     * Validates whether the castling move is valid for the given board state, move details, and king piece.
     *
     * @param board The current state of the chessboard.
     * @param move  The move object containing the details of the castling move.
     * @param king  The king piece that is attempting to castle.
     * @return true if the castling move is valid, false otherwise.
     */
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

    /**
     * Determines if a square on the chessboard is attacked by any piece of a given color.
     *
     * @param board the current state of the chessboard, including the positions of all pieces
     * @param targetRow the row index of the square to evaluate
     * @param targetCol the column index of the square to evaluate
     * @param attackingColor the color of the pieces that are considered attackers
     * @return true if the square is attacked by any piece of the specified color, false otherwise
     */
    boolean isSquareAttacked(Board board, int targetRow, int targetCol, Color attackingColor) {
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

    /**
     * Validates if the given movement constitutes a valid attack move by a piece of the specified type
     * on the board. The validation checks if the move complies with the rules of the specific piece
     * and ensures the destination satisfies attack conditions.
     *
     * @param board the board object representing the current state of the game
     * @param startRow the starting row position of the piece
     * @param startCol the starting column position of the piece
     * @param endRow the ending row position of the move
     * @param endCol the ending column position of the move
     * @param type the type of the piece attempting the movement
     * @return true if the movement is a valid attack for the specified piece type, false otherwise
     */
    private boolean isValidAttackMovement(Board board, int startRow, int startCol, int endRow, int endCol, PieceType type) {
        Move move = new Move(startRow, startCol, endRow, endCol);

        Piece attacker = board.getPieceAt(startRow, startCol);
        if (!isDestinationValid(board, move, attacker)) {
            return false;
        }

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

    /**
     * Validates whether a pawn attack move adheres to the rules of chess.
     *
     * @param board the current state of the chessboard
     * @param move the move to be validated, including start and end positions
     * @return true if the pawn attack is valid, false otherwise
     */
    private boolean validatePawnAttack(Board board, Move move){
        int direction = board.getPieceAt(move.getStartRow(), move.getStartCol()).getColor() == Color.WHITE ? -1 : 1;
        int rowDifference = move.getEndRow() - move.getStartRow();
        int colDifference = Math.abs(move.getEndCol() - move.getStartCol());

        return colDifference == 1 && rowDifference == direction;
    }
}