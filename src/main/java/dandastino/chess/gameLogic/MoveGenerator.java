package dandastino.chess.gameLogic;

import dandastino.chess.moves.Move;
import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;
import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    private final MoveValidator validator = new MoveValidator();

    /**
     * Generates a list of pseudo-legal moves for the current player given the board state.
     * Pseudo-legal moves are moves that ignore rules like check and checkmate but conform to
     * other movement rules for each piece type.
     *
     * @param board the current state of the chessboard, including piece positions and turn information
     * @return a list of all pseudo-legal moves available for the player whose turn it is to move
     */
    public List<Move> generatePseudoLegalMoves(Board board) {
        List<Move> pseudoMoves = new ArrayList<>();
        Color playerColor = board.isWhiteToMove() ? Color.WHITE : Color.BLACK;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPieceAt(r, c);
                if (piece != null && piece.getColor() == playerColor) {
                    addMovesForPiece(board, r, c, piece, pseudoMoves);
                }
            }
        }
        return pseudoMoves;
    }

    /**
     * Generates and adds all pseudo-legal moves for a specific piece on the chessboard
     * to the provided list of moves. It iterates through all possible target positions
     * on the board and validates each move based on the piece's type and the current board state.
     *
     * @param board the current state of the chessboard, including positions of all pieces
     * @param r the row index of the piece's current position
     * @param c the column index of the piece's current position
     * @param piece the chess piece for which moves are being generated
     * @param moves the list to which validated pseudo-legal moves will be added
     */
    private void addMovesForPiece(Board board, int r, int c, Piece piece, List<Move> moves) {
        for (int targetR = 0; targetR < 8; targetR++) {
            for (int targetC = 0; targetC < 8; targetC++) {
                Move move = new Move(r, c, targetR, targetC);

                // We bypass the "is it the right turn" check by calling
                // the specific validator for the piece type
                boolean isValid = switch (piece.getType()) {
                    case PAWN -> validator.validatePawnMove(board, move);
                    case KNIGHT -> validator.validateKnightMove(board, move);
                    case BISHOP -> validator.validateBishopMove(board, move);
                    case ROOK -> validator.validateRookMove(board, move);
                    case QUEEN -> validator.validateQueenMove(board, move);
                    case KING -> validator.validateKingMove(board, move);
                };

                if (isValid) {
                    moves.add(move);
                }
            }
        }
    }
}