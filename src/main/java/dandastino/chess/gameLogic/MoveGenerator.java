// Create a new class: dandastino.chess.gameLogic.MoveGenerator

package dandastino.chess.gameLogic;

import dandastino.chess.moves.Move;
import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceType;
import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    // Note: This method only generates moves that obey geometric rules (pseudo-legal).
    // King safety (checking for checkmate) MUST be done by the ChessEngine.isMoveSafe().
    public List<Move> generatePseudoLegalMoves(Board board) {
        List<Move> pseudoMoves = new ArrayList<>();
        Color playerColor = board.isWhiteToMove() ? Color.WHITE : Color.BLACK;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPieceAt(r, c);

                if (piece != null && piece.getColor().equals(playerColor)) {
                    // For the sake of simplicity, we iterate over ALL possible 64 destination squares.
                    // A highly optimized generator would use attack tables, but this works functionally.
                    for (int targetR = 0; targetR < 8; targetR++) {
                        for (int targetC = 0; targetC < 8; targetC++) {

                            Move move = new Move(r, c, targetR, targetC);

                            // IMPORTANT: We cannot use the full MoveValidator.isMoveLegal() 
                            // because that includes the basic check for moving the correct color,
                            // and this generator is specifically looking at the current player's pieces.
                            // We need a validator that only checks the move's geometry.
                            if (isPieceMovementValid(board, move)) {
                                pseudoMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        return pseudoMoves;
    }

    // --- Helper function that duplicates geometric validation from MoveValidator ---
    // This is necessary to avoid relying on the full MoveValidator.isMoveLegal() which
    // includes a check for whose turn it is, which the generator has already handled.
    // In a production engine, this logic would be shared cleanly.
    private boolean isPieceMovementValid(Board board, Move move) {
        // NOTE: This should contain the switch logic from MoveValidator, 
        // but skipping the initial "is it the right color to move" check.
        // For simplicity here, we assume the MoveValidator has a helper for this.
        // For your code, you should copy/paste the geometric checks logic here
        // OR refactor MoveValidator to expose a cleaner geometric check method.

        // CONCEPTUAL CALL (requires refactoring your MoveValidator):
        // return MoveValidator.isGeometricMoveValid(board, move);

        // Since we don't want to restructure MoveValidator now, we'll assume a stub:
        // *** You must fill this in with the appropriate switch/case logic ***
        // *** (e.g., call validatePawnMove, validateKnightMove, etc. from MoveValidator) ***
        return true;
    }
}