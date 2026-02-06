package dandastino.chess.gameLogic;

import dandastino.chess.moves.Move;
import dandastino.chess.moves.MoveDTO;
import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceType;

import java.util.List;

public class ChessEngine {
    private final Board board;
    private final MoveValidator validator = new MoveValidator();
    private final MoveGenerator generator = new MoveGenerator();

    public ChessEngine(String fen) {
        this.board = FenParser.parse(fen);
    }

    /**
     * Generates the Standard Algebraic Notation (SAN) for a given move on the chess board.
     * This notation includes information such as castling, captures, check/checkmate,
     * and pawn promotions.
     *
     * @param board the current game board on which the move is being performed
     * @param move the move to be converted into SAN notation
     * @return the SAN representation of the provided move
     */
    public String generateSan(Board board, Move move) {
        Piece piece = board.getPieceAt(move.getStartRow(), move.getStartCol());
        if (piece == null) return "??";

        // 1. Castling
        if (piece.getType().equals(PieceType.KING) && Math.abs(move.getEndCol() - move.getStartCol()) == 2) {
            return move.getEndCol() > move.getStartCol() ? "O-O" : "O-O-O";
        }

        // 2. Capture and Target Info
        Piece targetPiece = board.getPieceAt(move.getEndRow(), move.getEndCol());
        boolean isCapture = targetPiece != null || piece.getType().equals(PieceType.PAWN) && move.getStartCol() != move.getEndCol();
        String targetSquare = BoardUtils.toChessNotation(move.getEndRow(), move.getEndCol());

        String san = "";

        // 3. Piece Logic (Pawn vs Others)
        if (piece.getType().equals(PieceType.PAWN)) {
            san = generatePawnMoveSan(move, isCapture, targetSquare);
            // Promotion suffix
            if (move.getEndRow() == 0 || move.getEndRow() == 7) {
                san += "=" + getPromotionChar(piece.getColor());
            }
        } else {
            String pieceSymbol = getPieceSymbol(piece.getType());
            String disambiguation = getDisambiguation(board, move, piece);
            san = pieceSymbol + disambiguation + (isCapture ? "x" : "") + targetSquare;
        }

        // 4. Check/Checkmate Suffixes
        if (isCheckmateAfterMove(board, move)) {
            san += "#";
        } else if (isCheckAfterMove(board, move)) {
            san += "+";
        }

        return san;
    }

    /**
     * Determines the disambiguation string for a chess move in cases where multiple pieces
     * of the same type and color could move to the same destination square. The disambiguation
     * is generated based on file, rank, or both depending on the position of the pieces.
     *
     * @param board the chess board representing the current game state
     * @param move the move being made, containing the start and end positions
     * @param movingPiece the piece that is intended to make the move
     * @return a string representing the disambiguation, which could be a file, rank,
     *         both file and rank, or an empty string if no disambiguation is needed
     */
    private String getDisambiguation(Board board, Move move, Piece movingPiece) {
        boolean fileNeeded = false;
        boolean rankNeeded = false;
        boolean anyRival = false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece rival = board.getPieceAt(r, c);
                if (rival != null && rival.getType() == movingPiece.getType() &&
                        rival.getColor() == movingPiece.getColor() && !(r == move.getStartRow() && c == move.getStartCol())) {

                    Move rivalMove = new Move(r, c, move.getEndRow(), move.getEndCol());
                    // Use isMoveSafe to ensure we only disambiguate against legal moves
                    if (isMoveSafe(rivalMove)) {
                        anyRival = true;
                        if (c == move.getStartCol()) rankNeeded = true;
                        else fileNeeded = true;
                    }
                }
            }
        }

        if (!anyRival) return "";
        if (fileNeeded && rankNeeded) return BoardUtils.toChessNotation(move.getStartRow(), move.getStartCol());
        if (fileNeeded) return String.valueOf((char) ('a' + move.getStartCol()));

        return String.valueOf(8 - move.getStartRow());

    }

    /**
     * Determines whether the current player's king is in check on the chessboard.
     * A king is considered to be in check if it is under attack by any piece of the opposing color.
     *
     * @return true if the current player's king is in check, false otherwise
     */
    public boolean isCheck() {
        Color currentPlayer = board.isWhiteToMove() ? Color.WHITE : Color.BLACK;
        Color opponent = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
        int[] kingPos = findKing(board, currentPlayer);
        return validator.isSquareAttacked(board, kingPos[0], kingPos[1], opponent);
    }

    /**
     * Determines whether a given chess move is safe to make, meaning it does not put the player's own king in check.
     *
     * @param move the move to be evaluated, containing the start and end positions as well as other move details
     * @return true if the move is legal and the player's king remains safe after performing the move, false otherwise
     */
    private boolean isMoveSafe(Move move) {
        if (!validator.isMoveLegal(board, move)) return false;

        Board futureBoard = board.copy();
        futureBoard.MovePiece(move.getStartRow(), move.getEndRow(), move.getStartCol(), move.getEndCol());

        // We must check the safety of the player who JUST moved
        Color movingColor = board.getPieceAt(move.getStartRow(), move.getStartCol()).getColor();
        return !isKingInCheckOnBoard(futureBoard, movingColor);
    }

    /**
     * Determines whether the king of a specified color is in check on the provided chessboard.
     * A king is in check if it is under attack by any piece of the opposing color.
     *
     * @param testBoard the chessboard to evaluate for the presence of a check
     * @param kingColor the color of the king being checked, either white or black
     * @return true if the king of the specified color is in check, false otherwise
     */
    private boolean isKingInCheckOnBoard(Board testBoard, Color kingColor) {
        int[] kingPos = findKing(testBoard, kingColor);
        Color attackerColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return validator.isSquareAttacked(testBoard, kingPos[0], kingPos[1], attackerColor);
    }

    /**
     * Finds the position of the king of the specified color on the given chessboard.
     * The king's position is returned as an array containing the row and column indices.
     * If no king of the specified color is found, the method returns an array {-1, -1}.
     *
     * @param testBoard the chessboard to search for the king
     * @param kingColor the color of the king to locate, either white or black
     * @return an array containing the row and column of the king's position,
     *         or {-1, -1} if the king is not found
     */
    private int[] findKing(Board testBoard, Color kingColor) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = testBoard.getPieceAt(r, c);
                if (p != null && p.getType() == PieceType.KING && p.getColor() == kingColor) {
                    return new int[]{r, c};
                }
            }
        }
        return new int[]{-1, -1};
    }

    /**
     * Generates the Standard Algebraic Notation (SAN) for a pawn move.
     * Includes information on whether the pawn makes a capture or just moves to the target square.
     *
     * @param move the move being performed, containing the start and end positions
     * @param isCapture a boolean indicating if the move involves capturing an opponent's piece
     * @param targetSquare the target square's notation (e.g., "e4") where the pawn is moving
     * @return a string representing the SAN of the pawn move, including capture notation if applicable
     */
    private String generatePawnMoveSan(Move move, boolean isCapture, String targetSquare) {
        if (isCapture) {
            return (char) ('a' + move.getStartCol()) + "x" + targetSquare;
        }
        return targetSquare;
    }

    /**
     * Retrieves the symbol representing the specified chess piece type.
     *
     * @param type the type of the chess piece (e.g., KING, QUEEN, ROOK, BISHOP, KNIGHT, or PAWN)
     * @return the symbol representing the chess piece type, such as "K" for king,
     *         "Q" for queen, and "N" for knight. Returns an empty string
     *         if the piece type is not recognized.
     */
    private String getPieceSymbol(PieceType type) {
        return switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            default -> "";
        };
    }

    /**
     * Retrieves the default character used for pawn promotion based on the provided piece color.
     * Currently, the method always returns "Q" (for Queen), irrespective of the color input.
     *
     * @param color the color of the pawn being promoted, either WHITE or BLACK
     * @return the character "Q", representing the piece promoted to (Queen)
     */
    private String getPromotionChar(Color color) {
        return "Q";
    }

    /**
     * Determines whether the current player's king is in checkmate.
     * A checkmate occurs when the current player's king is in check and there are no
     * legal moves available to remove the king from check.
     *
     * @return true if the current player's king is in checkmate, false otherwise
     */
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

    /**
     * Determines whether the current game is in a drawn state according to chess rules.
     * The method checks various conditions that can lead to a draw:
     * 1. If the current player is not in check and has no legal moves (stalemate).
     * 2. If the 50-move rule applies (50 full moves with no pawn moves or captures).
     *
     * Additional rules such as threefold repetition and insufficient material
     * may also apply, but these are not explicitly implemented in this method.
     *
     * @return true if the game is in a draw state, false otherwise
     */
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
        return board.getHalfMoveClock() >= 100;

        // Threefold Repetition (Requires history tracking, often stored in the Board/Game class)

        // Insufficient Material (e.g., King vs King, King vs Knight/Bishop)

    }

    /**
     * Executes a chess move from the provided start square to the target square, performing necessary
     * validations such as legality and king safety, and updates the game state accordingly. Returns
     * a detailed result encapsulated in a {@code MoveDTO} object.
     *
     * @param from the starting position of the piece to be moved, in standard chess notation (e.g., "e2")
     * @param to the target position of the piece to be moved, in standard chess notation (e.g., "e4")
     * @return a {@code MoveDTO} containing the results and state information before and after the move,
     *         including legality, check/checkmate status, and updated FEN string
     */
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

    /**
     * Generates a new FEN (Forsyth-Edwards Notation) string representing the current state
     * of the chessboard. The FEN string captures the board configuration, active player,
     * castling rights, en passant target square, half-move clock, and full-move number.
     *
     * @return a string in FEN format representing the current state of the chessboard.
     */
    public String generateNewFen() {
        return board.toFen();
    }

    /**
     * Retrieves the default promotion piece character based on the color of the pawn being promoted.
     * The method returns "Q" for a white pawn and "q" for a black pawn, corresponding to promotion to a Queen.
     *
     * @param move the move involving the pawn promotion, containing the starting position of the pawn
     * @return a string representing the piece to which the pawn is promoted ("Q" for white, "q" for black)
     */
    private String getPromotionChar(Move move) {
        // Determine the color of the pawn being promoted
        Color color = board.getPieceAt(move.getStartRow(), move.getStartCol()).getColor();

        // Default to Queen ('Q' or 'q')
        return color.equals(Color.WHITE) ? "Q" : "q";
    }

    /**
     * Generates the Standard Algebraic Notation (SAN) string for a pawn move.
     *
     * @param board The current state of the chessboard.
     * @param move The move to be converted into SAN notation.
     * @return A string representing the pawn move in SAN notation, including capture notation if applicable.
     */
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

    /**
     * Determines if the opponent's King will be in check after a specified move is made.
     *
     * @param board the current game board before the move is made
     * @param move the move to be simulated on the board
     * @return true if the opponent's King is in check after the move, false otherwise
     */
    private boolean isCheckAfterMove(Board board, Move move) {
        Color opponentColor = board.isWhiteToMove() ? Color.BLACK : Color.WHITE;

        // Simulate the move
        Board futureBoard = board.copy();
        futureBoard.MovePiece(move.getStartRow(), move.getEndRow(), move.getStartCol(), move.getEndCol());

        // Check if the opponent's King is now in check on the future board.
        return isKingInCheckOnBoard(futureBoard, opponentColor);
    }

    /**
     * Determines if a move results in a checkmate on the chess board.
     * This method simulates the given move, evaluates the board state,
     * and checks if the resulting position leads to a checkmate.
     *
     * @param board the current chess board state before the move
     * @param move the move to be simulated and evaluated
     * @return true if the move results in a checkmate, false otherwise
     */
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