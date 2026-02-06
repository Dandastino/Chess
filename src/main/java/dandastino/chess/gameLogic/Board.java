package dandastino.chess.gameLogic;

import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceType;

public class Board {
    private final Piece[][] squares = new Piece[8][8];
    // 'r','n','b','q','k','p' (black lower)
    // 'R','N','B','Q','K','P' (white upper)
    // '.' = empty

    private boolean whiteToMove;

    private boolean whiteKingSideCastle;
    private boolean whiteQueenSideCastle;
    private boolean blackKingSideCastle;
    private boolean blackQueenSideCastle;

    // En passant square (e.g. "e3" or null)
    private String enPassantSquare;

    // 50-move rule counters
    private int halfMoveClock;
    private int fullMoveNumber;

    public Board() {
    }

    /**
     * Creates a chessboard object based on the provided FEN (Forsyth-Edwards Notation) string.
     * Parses the FEN string to initialize the board's state, including piece positions,
     * castling rights, en passant square, move counters, and the current turn.
     *
     * @param fen the FEN string representing the state of a chessboard.
     *            It should be a valid FEN string formatted with space-separated parts indicating
     *            piece placement, active player, castling availability, en passant square,
     *            half_move clock, and full_move number.
     * @return the initialized Board object that corresponds to the state described by the FEN string.
     */
    public static Board fromFen(String fen){
        return FenParser.parse(fen);
    }

    /**
     * Converts the current state of the chessboard into a FEN (Forsyth-Edwards Notation) string.
     * The FEN string represents the board configuration, active player, castling rights,
     * en passant square, half_move clock, and full_move number.
     *
     * @return a string in FEN format representing the current state of the chessboard.
     */
    public String toFen(){
        return FenParser.toFen(this);
    }

    /**
     *
     * Retrieves the piece located at the specified row and column on the board.
     *
     * @param row the row index of the desired square, where 0 is the top row and 7 is the bottom row
     * @param col the column index of the desired square, where 0 is the leftmost column and 7 is the rightmost column
     * @return the piece at the specified location, or null if the indices are out of bounds or the square is empty
     */
    public Piece getPieceAt(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return null;
        }

        return squares[row][col];
    }

    /**
     * Places a specified chess piece on the board at a given row and column.
     *
     * @param row the row index where the piece should be placed, ranging from 0 (top row) to 7 (bottom row)
     * @param col the column index where the piece should be placed, ranging from 0 (leftmost column) to 7 (rightmost column)
     * @param piece the chess piece to be placed on the board at the specified location; can be null to clear the square
     */
    public void setPiece(int row, int col, Piece piece) {
        if (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
            this.squares[row][col] = piece;
        }
    }

    /**
     * Moves a piece from one position on the chessboard to another, handling various special rules such as castling,
     * en passant, and resetting or updating state variables like the move clock and en passant square.
     *
     * @param fromRow the row index of the square where the piece is currently located, ranging from 0 to 7
     * @param toRow the row index of the target square where the piece will be moved, ranging from 0 to 7
     * @param fromCol the column index of the square where the piece is currently located, ranging from 0 to 7
     * @param toCol the column index of the target square where the piece will be moved, ranging from 0 to 7
     */
    public void MovePiece(int fromRow, int toRow, int fromCol, int toCol) {
        Piece pieceToMove = getPieceAt(fromRow, fromCol);
        if (pieceToMove == null) return;

        // GUARD: Prevent moving out of turn
        boolean isWhitePiece = pieceToMove.getColor().equals(Color.WHITE);
        if (isWhitePiece != this.whiteToMove) {
            return;
            // maybe exception?
        }

        Piece capturedPiece = getPieceAt(toRow, toCol);

        // Castling (Special move for the King and Rook)
        if (pieceToMove.getType().equals(PieceType.KING) && Math.abs(toCol - fromCol) == 2) {
            handleCastlingMove(fromRow, fromCol, toCol);
        }

        // Checks if the move is a diagonal pawn move to an empty square that matches the enPassantSquare.
        if (pieceToMove.getType().equals(PieceType.PAWN) && toCol != fromCol && capturedPiece == null &&
                BoardUtils.toChessNotation(toRow, toCol).equals(this.enPassantSquare)) {

            // The captured pawn is on the row of the original pawn and the column of the destination.
            squares[fromRow][toCol] = null;
        }

        squares[toRow][toCol] = pieceToMove;
        squares[fromRow][fromCol] = null;

        // Update Piece State: set hasMoved to true
        if (!pieceToMove.hasMoved()) {
            pieceToMove.setHasMoved(true);
        }

        // Update Castling Rights (If King or Rook moved)
        updateCastlingRights(pieceToMove, fromRow, fromCol);

        // Update half_move_clock (50-move rule)
        if (pieceToMove.getType().equals(PieceType.PAWN) || capturedPiece != null) {
            this.halfMoveClock = 0; // Reset clock on pawn move or capture
        } else {
            this.halfMoveClock++; // Increment otherwise
        }

        // Update En Passant Square
        this.enPassantSquare = updateEnPassantSquare(pieceToMove, fromRow, toRow, fromCol);

        // Update full_move Number and Turn
        if (pieceToMove.getColor().equals(Color.BLACK)) {
            this.fullMoveNumber++;
        }

        this.whiteToMove = !this.whiteToMove; // Flip the turn
    }

    /**
     * Determines whether it is White's turn to move.
     *
     * @return true if it is White's turn to move, false if it is Black's turn.
     */
    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    /**
     * Creates a deep copy of the current board. All state variables and pieces
     * are duplicated to ensure no shared references between the original and the copied board.
     *
     * @return a new Board object that is a deep copy of the current board
     */
    public Board copy() {
        Board copy = new Board();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece originalPiece = this.squares[r][c];
                if (originalPiece != null) {
                    copy.squares[r][c] = originalPiece.clone();
                } else {
                    copy.squares[r][c] = null;
                }
            }
        }

        copy.whiteToMove = this.whiteToMove;
        copy.whiteKingSideCastle = this.whiteKingSideCastle;
        copy.whiteQueenSideCastle = this.whiteQueenSideCastle;
        copy.blackKingSideCastle = this.blackKingSideCastle;
        copy.blackQueenSideCastle = this.blackQueenSideCastle;
        copy.enPassantSquare = this.enPassantSquare;
        copy.halfMoveClock = this.halfMoveClock;
        copy.fullMoveNumber = this.fullMoveNumber;

        return copy;
    }

    /**
     * Handles the logic for executing a castling move during a chess game. It moves the rook
     * involved in castling to the correct position based on whether the move is a kingside
     * or queenside castle. The method assumes that all the rules for castling eligibility
     * have already been validated.
     *
     * @param row the row index of the starting position of the king, where 0 represents
     *                the top row and 7 represents the bottom row
     * @param fromCol the column index of the starting position of the king, where 0 represents
     *                the leftmost column and 7 represents the rightmost column
     * @param toCol the column index of the target position of the king, where 0 represents
     *              the leftmost column and 7 represents the rightmost column
     */
    private void handleCastlingMove(int row, int fromCol, int toCol) {
        int rookStartCol, rookEndCol;

        if (toCol > fromCol) { // Kingside (short) castle
            rookStartCol = 7;
            rookEndCol = 5;
        } else { // Queenside (long) castle
            rookStartCol = 0;
            rookEndCol = 3;
        }

        Piece rook = getPieceAt(row, rookStartCol);

        if (rook != null) {
            squares[row][rookEndCol] = rook;
            squares[row][rookStartCol] = null;
            rook.setHasMoved(true);
        }
    }

    /**
     * Updates the castling rights for the associated player based on the movement of a specific piece.
     * If the king moves, both kingside and queenside castling rights for that color are revoked.
     * If a rook moves from its initial position, the corresponding castling rights (kingside or queenside)
     * for that color are revoked.
     *
     * @param piece the piece being moved; its type and color determine the updates to castling rights
     * @param fromRow the row index from which the piece is moved, where 0 is the top row and 7 is the bottom row
     * @param fromCol the column index from which the piece is moved, where 0 is the leftmost column and 7 is the rightmost column
     */
    private void updateCastlingRights(Piece piece, int fromRow, int fromCol) {
        if (piece.getType().equals(PieceType.KING)) {
            if (piece.getColor().equals(Color.WHITE)) {
                this.whiteKingSideCastle = false;
                this.whiteQueenSideCastle = false;
            } else {
                this.blackKingSideCastle = false;
                this.blackQueenSideCastle = false;
            }
        } else if (piece.getType().equals(PieceType.ROOK)) {
            if (piece.getColor().equals(Color.WHITE)) {
                if (fromCol == 7) this.whiteKingSideCastle = false; // Kingside Rook moved
                if (fromCol == 0) this.whiteQueenSideCastle = false; // Queenside Rook moved
            } else {
                if (fromCol == 7) this.blackKingSideCastle = false; // Kingside Rook moved
                if (fromCol == 0) this.blackQueenSideCastle = false; // Queenside Rook moved
            }
        }
    }

    /**
     * Updates the en passant square based on the movement of a pawn. If a pawn performs a two-square
     * advance, this method calculates and returns the en passant square in chess notation.
     * Otherwise, it returns the standard marker for no en passant square ("-").
     *
     * @param piece the chess piece being moved; it is checked whether it is a pawn
     * @param fromRow the row index from which the piece is moved, ranging from 0 (top row) to 7 (bottom row)
     * @param toRow the row index to which the piece is moved, ranging from 0 (top row) to 7 (bottom row)
     * @param fromCol the column index from which the piece is moved, ranging from 0 (leftmost column) to 7 (rightmost column)
     * @return the en passant square in chess notation if a pawn performs a valid two-square advance;
     *         otherwise, returns "-"
     */
    private String updateEnPassantSquare(Piece piece, int fromRow, int toRow, int fromCol) {
        if (piece.getType().equals(PieceType.PAWN) && Math.abs(toRow - fromRow) == 2) {
            int passingRow = fromRow + (piece.getColor().equals(Color.WHITE) ? -1 : 1);

            return BoardUtils.toChessNotation(passingRow, fromCol);
        }

        return "-"; // FEN standard for "no en passant square"
    }

    /**
     * Retrieves the current castling rights for both players in the chess game.
     * The castling rights are represented by the following characters:
     * 'K' for white kingside, 'Q' for white queenside,
     * 'k' for black kingside, and 'q' for black queenside.
     * If no castling rights are available, the method returns "-".
     *
     * @return a string indicating the current castling rights. Returns
     *         "KQkq", a subset of these characters, or "-" if no castling rights exist.
     */
    public String getCastlingRights() {
        StringBuilder sb = new StringBuilder();
        if (whiteKingSideCastle) sb.append('K');
        if (whiteQueenSideCastle) sb.append('Q');
        if (blackKingSideCastle) sb.append('k');
        if (blackQueenSideCastle) sb.append('q');
        return !sb.isEmpty() ? sb.toString() : "-";
    }

    /**
     * Updates the castling rights for both players based on the provided FEN (Forsyth-Edwards Notation) castling string.
     * The string should include 'K' for white kingside castling, 'Q' for white queenside castling,
     * 'k' for black kingside castling, and 'q' for black queenside castling. If any of these characters
     * are absent, the respective castling rights are revoked.
     *
     * @param fenCastling the FEN castling rights string, which specifies available castling options.
     *                    It should contain a combination of 'K', 'Q', 'k', 'q', or "-" if no rights exist.
     */
    public void setCastlingRights(String fenCastling) {
        this.whiteKingSideCastle = fenCastling.contains("K");
        this.whiteQueenSideCastle = fenCastling.contains("Q");
        this.blackKingSideCastle = fenCastling.contains("k");
        this.blackQueenSideCastle = fenCastling.contains("q");
    }

    // --- En Passant ---
    public String getEnPassantSquare() {
        return enPassantSquare;
    }
    public void setEnPassantSquare(String enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }

    // --- Clocks ---
    public int getHalfMoveClock() {
        return halfMoveClock;
    }
    public void setHalfMoveClock(int halfMoveClock) {
        this.halfMoveClock = halfMoveClock;
    }
    public int getFullMoveNumber() {
        return fullMoveNumber;
    }
    public void setFullMoveNumber(int fullMoveNumber) {
        this.fullMoveNumber = fullMoveNumber;
    }

    // --- Turn ---
    public void setWhiteToMove(boolean whiteToMove) {
        this.whiteToMove = whiteToMove;
    }


}
