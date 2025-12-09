package dandastino.chess.gameLogic;

import dandastino.chess.piece.BoardUtils;
import dandastino.chess.piece.Color;
import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceType;

public class Board {
    private final Piece[][] squares = new Piece[8][8];
    // 'r','n','b','q','k','p' (black lower)
    // 'R','N','B','Q','K','P' (white upper)
    // '.' = empty

    private boolean whiteToMove;

    private static final int RANK_8 = 0; // Black's start rank
    private static final int RANK_1 = 7; // White's start rank

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

    public static Board fromFen(String fen){
        return FenParser.parse(fen);
    }

    public String toFen(){
        return FenParser.toFen(this);
    }

    public Piece getPieceAt(int row, int col) {
        // Essential boundary check
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return null;
        }

        return squares[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        if (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
            this.squares[row][col] = piece;
        }
    }

// Inside the Board class:

    public void MovePiece(int fromRow, int toRow, int fromCol, int toCol) {
        Piece pieceToMove = getPieceAt(fromRow, fromCol);
        if (pieceToMove == null) return;

        Piece capturedPiece = getPieceAt(toRow, toCol);

        // --- 1. SPECIAL MOVE HANDLING (Must happen BEFORE the piece is moved) ---

        // 1a. Castling (Special move for the King and Rook)
        if (pieceToMove.getType().equals(PieceType.KING) && Math.abs(toCol - fromCol) == 2) {
            // Implement logic to move the Rook during castling.
            // You would determine which side (king or queen) and call setPiece for the Rook.
            // Example: If white kingside castling, move Rook from (7, 7) to (7, 5).
            handleCastlingMove(fromRow, fromCol, toRow, toCol);
        }

        // 1b. En Passant Capture (Requires updating the captured square)
        // Checks if the move is a diagonal pawn move to an empty square that matches the enPassantSquare.
        if (pieceToMove.getType().equals(PieceType.PAWN) && toCol != fromCol && capturedPiece == null &&
                BoardUtils.toChessNotation(toRow, toCol).equals(this.enPassantSquare)) {

            // The piece captured during en passant is the one behind the target square.
            int capturedPawnRow = fromRow; // Same row as the moving pawn
            int capturedPawnCol = toCol;   // Same column as the target square
            squares[capturedPawnRow][capturedPawnCol] = null; // Remove the captured pawn
        }

        // --- 2. PERFORM THE MOVE (Main action) ---
        squares[toRow][toCol] = pieceToMove;
        squares[fromRow][fromCol] = null;

        // --- 3. UPDATE STATE AFTER MOVE ---

        // 3a. Update Piece State: set hasMoved to true
        if (!pieceToMove.hasMoved()) {
            pieceToMove.setHasMoved(true);
        }

        // 3b. Update Castling Rights (If King or Rook moved)
        updateCastlingRights(pieceToMove, fromRow, fromCol);

        // 3c. Update Halfmove Clock (50-move rule)
        if (pieceToMove.getType().equals(PieceType.PAWN) || capturedPiece != null) {
            this.halfMoveClock = 0; // Reset clock on pawn move or capture
        } else {
            this.halfMoveClock++; // Increment otherwise
        }

        // 3d. Update En Passant Square
        String newEnPassantSquare = updateEnPassantSquare(pieceToMove, fromRow, toRow, fromCol);
        this.enPassantSquare = newEnPassantSquare;

        // 3e. Update Fullmove Number and Turn
        if (pieceToMove.getColor().equals(Color.BLACK)) {
            this.fullMoveNumber++;
        }
        this.whiteToMove = !this.whiteToMove; // Flip the turn
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }
    public void setWhiteToMove(boolean whiteToMove) {
        this.whiteToMove = whiteToMove;
    }

    // --- Castling ---
// NOTE: For FEN parsing/generating, you should have a single String getter/setter
// that combines/parses the four boolean flags (KQkq).
    public String getCastlingRights() {
        // Logic to combine the four booleans into a FEN string (e.g., "KQkq", "Kq", or "-")
        StringBuilder sb = new StringBuilder();
        if (whiteKingSideCastle) sb.append('K');
        if (whiteQueenSideCastle) sb.append('Q');
        if (blackKingSideCastle) sb.append('k');
        if (blackQueenSideCastle) sb.append('q');
        return sb.length() > 0 ? sb.toString() : "-";
    }
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
    
    public Board copy() {
        Board copy = new Board();

        // 1. Deep copy the piece positions
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece originalPiece = this.squares[r][c];
                if (originalPiece != null) {
                    // ⚠️ IMPORTANT: You must clone/create a new Piece object
                    // so changes to the copy don't affect the original board.
                    copy.squares[r][c] = originalPiece.clone();
                } else {
                    copy.squares[r][c] = null;
                }
            }
        }

        // 2. Copy all FEN-related state variables
        copy.whiteToMove = this.whiteToMove;
        copy.whiteKingSideCastle = this.whiteKingSideCastle;
        copy.whiteQueenSideCastle = this.whiteQueenSideCastle;
        copy.blackKingSideCastle = this.blackKingSideCastle;
        copy.blackQueenSideCastle = this.blackQueenSideCastle;
        copy.enPassantSquare = this.enPassantSquare; // String is immutable, so this is safe
        copy.halfMoveClock = this.halfMoveClock;
        copy.fullMoveNumber = this.fullMoveNumber;

        return copy;
    }

    private void handleCastlingMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rookStartCol, rookEndCol;

        // Determine the castling type based on the destination column
        if (toCol > fromCol) { // Kingside (short) castle
            rookStartCol = 7;
            rookEndCol = 5;
        } else { // Queenside (long) castle
            rookStartCol = 0;
            rookEndCol = 3;
        }

        // Get the Rook piece
        Piece rook = getPieceAt(fromRow, rookStartCol);

        // Move the Rook
        if (rook != null) {
            squares[fromRow][rookEndCol] = rook;
            squares[fromRow][rookStartCol] = null;
            rook.setHasMoved(true); // Crucial for future castling validation
        }
    }

    // Needs access to PieceType and Color enums.
    private void updateCastlingRights(Piece piece, int fromRow, int fromCol) {
        if (piece.getType().equals(PieceType.KING)) {
            // King move voids both castling rights for that color
            if (piece.getColor().equals(Color.WHITE)) {
                this.whiteKingSideCastle = false;
                this.whiteQueenSideCastle = false;
            } else {
                this.blackKingSideCastle = false;
                this.blackQueenSideCastle = false;
            }
        } else if (piece.getType().equals(PieceType.ROOK)) {
            // Rook move voids one castling right
            if (piece.getColor().equals(Color.WHITE)) {
                if (fromCol == 7) this.whiteKingSideCastle = false; // Kingside Rook moved
                if (fromCol == 0) this.whiteQueenSideCastle = false; // Queenside Rook moved
            } else {
                if (fromCol == 7) this.blackKingSideCastle = false; // Kingside Rook moved
                if (fromCol == 0) this.blackQueenSideCastle = false; // Queenside Rook moved
            }
        }
    }

    // Needs access to PieceType and BoardUtils.
    private String updateEnPassantSquare(Piece piece, int fromRow, int toRow, int fromCol) {
        if (piece.getType().equals(PieceType.PAWN) && Math.abs(toRow - fromRow) == 2) {
            // Pawn moved two squares, the en passant square is the one behind the destination
            int passingRow = fromRow + (piece.getColor().equals(Color.WHITE) ? -1 : 1);

            // Convert the passing row/col to chess notation (e.g., "e3")
            return BoardUtils.toChessNotation(passingRow, fromCol);
        }

        // If no two-square pawn move was made, the en passant opportunity is lost.
        return "-"; // FEN standard for "no en passant square"
    }


}
