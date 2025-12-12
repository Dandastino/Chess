package dandastino.chess.gameLogic;

import dandastino.chess.moves.Move;
import dandastino.chess.moves.MoveDTO;
import dandastino.chess.piece.BoardUtils;
import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceType;

import java.util.List; // Assumed for move generation

public class ChessEngine {
    private final Board board;
    private final MoveValidator validator = new MoveValidator();
    private final MoveGenerator generator = new MoveGenerator();

    public ChessEngine(String fen) {
        this.board = FenParser.parse(fen);
    }

    public String generateSan(Board board, Move move) {
        Piece piece = board.getPieceAt(move.getStartRow(), move.getStartCol());
        if (piece == null) return "??"; // Should not happen for a legal move


        // Castling
        if (piece.getType().equals(PieceType.KING) && Math.abs(move.getEndCol() - move.getStartCol()) == 2) {
            return move.getEndCol() == 6 ? "O-O" : "O-O-O";
        }

        // Pawn Promotion
        if (piece.getType().equals(PieceType.PAWN) && (move.getEndRow() == 0 || move.getEndRow() == 7)) {
             // Return something like "e8=Q"
             return generatePawnMoveSan(board, move) + "=" + getPromotionChar(move);
        }

        // Determine Action: Capture or Simple Move
        Piece targetPiece = board.getPieceAt(move.getEndRow(), move.getEndCol());
        boolean isCapture = targetPiece != null || (piece.getType().equals(PieceType.PAWN) && move.getEndCol() != move.getStartCol() && targetPiece == null); // En Passant is a capture to an empty square

        // Get Piece Symbol and Target Square
        String pieceSymbol = getPieceSymbol(piece.getType()); // K, Q, R, B, N, or "" for pawn
        String targetSquare = BoardUtils.toChessNotation(move.getEndRow(), move.getEndCol());

        // Handle Pawns (Special Case: No symbol, requires file for captures)
        if (piece.getType().equals(PieceType.PAWN)) {
            return generatePawnMoveSan(board, move, isCapture, targetSquare);
        }

        // andle Disambiguation (Crucial Step for non-Pawn moves)
        String disambiguation = getDisambiguation(board, move, piece);

        // Add Check/Checkmate Suffix (Requires simulating the move and checking)
        String san = pieceSymbol + disambiguation + (isCapture ? "x" : "") + targetSquare;
        if (isCheckAfterMove(board, move)) { san += "+"; }
        if (isCheckmateAfterMove(board, move)) { san += "#"; }

        return san;
    }

    private String getPieceSymbol(PieceType type) {
        return switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> ""; // Pawns have no symbol
            default -> "";
        };
    }

    private String generatePawnMoveSan(Board board, Move move, boolean isCapture, String targetSquare) {
        if (isCapture) {
            // Pawn captures use the file of the starting square (e.g., "exd5")
            char startFile = (char) ('a' + move.getStartCol());
            return startFile + "x" + targetSquare;
        } else {
            // Simple pawn moves just use the target square (e.g., "e4")
            return targetSquare;
        }
    }


    private String getDisambiguation(Board board, Move move, Piece movingPiece) {
        // Disambiguation is needed when two pieces of the same type can move to the same square.

        boolean fileNeeded = false;
        boolean rankNeeded = false;

        // Iterate over the whole board to find other pieces of the same type and color
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece potentialAttacker = board.getPieceAt(r, c);

                // Check if the piece is the same type and color, but NOT the moving piece
                if (potentialAttacker != null
                        && potentialAttacker.getType().equals(movingPiece.getType())
                        && potentialAttacker.getColor().equals(movingPiece.getColor())
                        && !(r == move.getStartRow() && c == move.getStartCol()))
                {
                    // Check if this other piece could also legally make this exact move
                    Move rivalMove = new Move(r, c, move.getEndRow(), move.getEndCol());

                    // IMPORTANT: We use the geometric validator, ignoring King safety.
                    if (new MoveValidator().isMoveLegal(board, rivalMove)) {

                        // A rival can make the move. We need disambiguation.
                        if (c == move.getStartCol()) {
                            // The rival is in the same column: we need the rank (row).
                            rankNeeded = true;
                        } else {
                            // The rival is in a different column: we just need the file (column).
                            fileNeeded = true;
                        }
                    }
                }
            }
        }

        // Construct the Disambiguation String
        StringBuilder sb = getStringBuilder(move, fileNeeded, rankNeeded);

        return sb.toString();
    }

    private static StringBuilder getStringBuilder(Move move, boolean fileNeeded, boolean rankNeeded) {
        StringBuilder sb = new StringBuilder();

        if (fileNeeded || rankNeeded) {
            if (fileNeeded && !rankNeeded) {
                // Use only the file (column) of the starting square (e.g., "Rfe1")
                sb.append((char) ('a' + move.getStartCol()));
            } else {
                // If rank is needed (or both file/rank), use file+rank (e.g., "R1e1")
                sb.append(BoardUtils.toChessNotation(move.getStartRow(), move.getStartCol()));
            }
        }
        return sb;
    }

    public boolean isCheck() {
        Color currentPlayerColor = board.isWhiteToMove() ? Color.WHITE : Color.BLACK;
        Color opponentColor = (currentPlayerColor == Color.WHITE) ? Color.BLACK : Color.WHITE;

        // 1. Find the King's current position
        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPieceAt(r, c);
                if (piece != null && piece.getType().equals(PieceType.KING) && piece.getColor().equals(currentPlayerColor)) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }

        if (kingRow == -1) return false; // Should not happen in a standard game

        // 2. Use the MoveValidator to check if the King's square is attacked.
        return validator.isSquareAttacked(board, kingRow, kingCol, opponentColor);
    }

    private boolean isMoveSafe(Move move) {
        // Geometric Check (Path, L-shape, etc.)
        if (!validator.isMoveLegal(board, move)) {
            return false;
        }

        // King Safety Check: Simulate the move and check for check.
        Color movingPieceColor = board.isWhiteToMove() ? Color.WHITE : Color.BLACK;

        // Create a copy of the board to simulate the move
        Board futureBoard = board.copy();

        // Execute the move on the copy (MovePiece handles special rules like en passant/castling)
        futureBoard.MovePiece(move.getStartRow(), move.getEndRow(), move.getStartCol(), move.getEndCol());

        // When checking for safety, we check if the King of the player who just moved is attacked.
        // We use a helper method to find the King's new position on the futureBoard.
        return !isKingInCheckOnBoard(futureBoard, movingPieceColor);
    }

    /** Helper to check if a specific King color is attacked on a given board state. */
    private boolean isKingInCheckOnBoard(Board testBoard, Color kingColor) {
        Color attackerColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;

        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = testBoard.getPieceAt(r, c);
                if (piece != null && piece.getType().equals(PieceType.KING) && piece.getColor().equals(kingColor)) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }

        return validator.isSquareAttacked(testBoard, kingRow, kingCol, attackerColor);
    }

    public boolean isCheckmate() {
        if (!isCheck()) {
            return false;
        }

        // Generate all pseudo-legal moves and filter them by safety (isMoveSafe).
        List<Move> allPossibleMoves = generator.generatePseudoLegalMoves(board); // Need to define this method externally

        for (Move move : allPossibleMoves) {
            if (isMoveSafe(move)) {
                return false;
            }
        }
        return true;
    }

    public boolean isDraw() {
        if (!isCheck()) {
            List<Move> allPossibleMoves = generator.generatePseudoLegalMoves(board);
            boolean hasLegalMove = false;
            for (Move move : allPossibleMoves) {
                if (isMoveSafe(move)) {
                    hasLegalMove = true;
                    break;
                }
            }
            if (!hasLegalMove) {
                return true;
            }
        }

        // 2. 50-Move Rule
        // 100 half-moves = 50 full moves
        return board.getHalfMoveClock() >= 100;

        // Threefold Repetition (Requires history tracking, often stored in the Board/Game class)

        // Insufficient Material (e.g., King vs King, King vs Knight/Bishop)

    }

    public MoveDTO makeMove(String from, String to) {
        Move move = BoardUtils.toMoveObject(from, to);
        String oldFen = board.toFen(); // Capture current FEN before any change

        // Final Legality Check (includes King Safety)
        if (!isMoveSafe(move)) {
            // Return DTO indicating the move failed
            return new MoveDTO(
                    oldFen,
                    oldFen, // FEN remains unchanged
                    false, // isLegal = false
                    isCheck(), // Check status before the move
                    false,
                    false,
                    false,
                    null, // SAN move isn't generated for illegal moves
                    from,
                    to
            );
        }

        // Execute the move
        board.MovePiece(move.getStartRow(), move.getEndRow(), move.getStartCol(), move.getEndCol());

        // Update Game Status
        boolean check = isCheck();
        boolean checkmate = false;
        boolean draw = isDraw();

        String sanMove = generateSan(board, move);

        if (check) {
            checkmate = isCheckmate();
        }

        return new MoveDTO(
                oldFen,
                board.toFen(),
                true,
                check,
                checkmate,
                (draw && !checkmate),
                draw,
                sanMove,
                from,
                to
        );
    }

    public String generateNewFen() {
        return board.toFen();
    }

    private String getPromotionChar(Move move) {
        // Determine the color of the pawn being promoted
        Color color = board.getPieceAt(move.getStartRow(), move.getStartCol()).getColor();

        // Default to Queen ('Q' or 'q')
        return color.equals(Color.WHITE) ? "Q" : "q";
    }

    private String generatePawnMoveSan(Board board, Move move) {
        boolean isCapture = board.getPieceAt(move.getEndRow(), move.getEndCol()) != null || // Standard capture
                (move.getEndCol() != move.getStartCol() && board.getEnPassantSquare() != null
                        && BoardUtils.toChessNotation(move.getEndRow(), move.getEndCol()).equals(board.getEnPassantSquare())); // En Passant

        String targetSquare = BoardUtils.toChessNotation(move.getEndRow(), move.getEndCol());

        if (isCapture) {
            // Pawn captures use the file of the starting square (e.g., "exd5")
            char startFile = (char) ('a' + move.getStartCol());
            return startFile + "x" + targetSquare;
        } else {
            // Simple pawn moves just use the target square (e.g., "e4")
            return targetSquare;
        }
    }

    private boolean isCheckAfterMove(Board board, Move move) {
        Color opponentColor = board.isWhiteToMove() ? Color.BLACK : Color.WHITE;

        // Simulate the move
        Board futureBoard = board.copy();
        futureBoard.MovePiece(move.getStartRow(), move.getEndRow(), move.getStartCol(), move.getEndCol());

        // Check if the opponent's King is now in check on the future board.
        return isKingInCheckOnBoard(futureBoard, opponentColor);
    }

    private boolean isCheckmateAfterMove(Board board, Move move) {
        //Simulate the move
        Board futureBoard = board.copy();
        futureBoard.MovePiece(move.getStartRow(), move.getEndRow(), move.getStartCol(), move.getEndCol());
        if (!isCheckAfterMove(board, move)) {
            return false;
        }

        ChessEngine futureEngine = new ChessEngine(futureBoard.toFen());
        return futureEngine.isCheckmate();
    }
}