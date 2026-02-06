package dandastino.chess.gameLogic;

import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceFactory;

public class Fen {
    private Board board;        // 8x8 board
    private boolean whiteToMove;
    private String castlingRights;
    private String enPassantSquare;
    private int halfMoveClock;
    private int fullMoveNumber;

    public Fen(String fen) {
        parseFen(fen);
    }

    /**
     * Translate a FEN string and sets up the entire state of the game.
     *
     * @param fen a string that represents the current state of the game
     */
    private void parseFen(String fen) {
        String[] parts = fen.split(" ");

        Board board = new Board();
        this.board = board;
        rank(parts, board);

        this.board = board;
        whiteToMove = parts[1].equals("w");
        castlingRights = parts[2];
        enPassantSquare = parts[3];
        halfMoveClock = Integer.parseInt(parts[4]);
        fullMoveNumber = Integer.parseInt(parts[5]);
    }

    /**
     * Parses the rank data in FEN notation and populates the provided board's pieces for each row.
     *
     * @param parts an array of strings representing parts of the FEN notation, where the first element contains the rank specification
     * @param board the chess board to be updated with pieces as defined by the FEN rank data
     */
    static void rank(String[] parts, Board board) {
        String[] ranks = parts[0].split("/");

        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : ranks[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    // FEN numbers represent empty squares, which are 'null' in a Piece[][]
                    col += Character.getNumericValue(c);
                } else {
                    Piece piece = PieceFactory.fromFenChar(c, row, col, parts[2]);
                    board.setPiece(row, col++, piece);
                }
            }
        }
    }

    /**
     * Converts the current state of the chess game into a FEN (Forsyth-Edwards Notation) string.
     * The FEN string includes the board position, active player turn, castling rights, en passant target square,
     * halfmove clock, and fullmove number.
     *
     * @return a string representing the current game state in FEN notation
     */
    public String toFenString() {
        String boardPart = stringBuilder(board);

        return boardPart +
                " " +
                (whiteToMove ? "w" : "b") +
                " " +
                castlingRights +
                " " +
                enPassantSquare +
                " " +
                halfMoveClock +
                " " +
                fullMoveNumber;
    }

    /**
     * Constructs a FEN-compatible segment of a chessboard's current state using a {@link StringBuilder}.
     * This method iterates through the rows and columns of the board, representing pieces
     * in Forsyth-Edwards Notation (FEN) format. It counts empty squares and appends the
     * respective values to the StringBuilder for later use in constructing a FEN string.
     *
     * @param board the chessboard instance from which the current position is to be processed
     */
    static String stringBuilder(Board board) {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col); // Use the getter method
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        sb.append(empty);
                        empty = 0;
                    }
                    sb.append(piece.getFenChar());
                }
            }
            if (empty > 0) sb.append(empty);
            if (row < 7) sb.append('/');
        }

        return sb.toString();
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    public void setWhiteToMove(boolean whiteToMove) {
        this.whiteToMove = whiteToMove;
    }

    public String getCastlingRights() {
        return castlingRights;
    }

    public void setCastlingRights(String castlingRights) {
        this.castlingRights = castlingRights;
    }

    public String getEnPassantSquare() {
        return enPassantSquare;
    }

    public void setEnPassantSquare(String enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }

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

    @Override
    public String toString() {
        return toFenString();
    }
}

