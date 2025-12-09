package dandastino.chess.gameLogic;

import dandastino.chess.piece.Piece;
import dandastino.chess.piece.PieceFactory;

public class Fen {
    private Board board;        // 8x8 board
    private boolean whiteToMove;
    private String castlingRights;
    private String enPassantSquare;
    private int halfmoveClock;
    private int fullmoveNumber;

    public Fen(String fen) {
        parseFen(fen);
    }

    private void parseFen(String fen) {
        String[] parts = fen.split(" ");

        // 1. Board
        Board board = new Board();
        this.board = board;
        String[] ranks = parts[0].split("/");

        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : ranks[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    // FEN numbers represent empty squares, which are 'null' in a Piece[][]
                    col += Character.getNumericValue(c);
                } else {
                    // Need a method to convert FEN char to a Piece object
                    Piece piece = PieceFactory.fromFenChar(c); // <--- NEW HELPER METHOD NEEDED
                    board.setPiece(row, col++, piece);
                }
            }
        }
        this.board = board; // Assign the initialized board to the instance field

        // 2. Side to move
        whiteToMove = parts[1].equals("w");

        // 3. Castling
        castlingRights = parts[2];

        // 4. En passant
        enPassantSquare = parts[3];

        // 5. Halfmove clock
        halfmoveClock = Integer.parseInt(parts[4]);

        // 6. Fullmove number
        fullmoveNumber = Integer.parseInt(parts[5]);
    }

    public String toFenString() {
        StringBuilder sb = new StringBuilder();

        // Board → FEN
        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col); // Use the getter method
                if (piece == null) { // Check for null (empty square)
                    empty++;
                } else {
                    if (empty > 0) {
                        sb.append(empty);
                        empty = 0;
                    }
                    // Need a method on the Piece to get its FEN character
                    sb.append(piece.getFenChar()); // <--- NEW METHOD NEEDED on Piece class
                }
            }
            if (empty > 0) sb.append(empty);
            if (row < 7) sb.append('/');
        }

        sb.append(" ")
                .append(whiteToMove ? "w" : "b")
                .append(" ")
                .append(castlingRights)
                .append(" ")
                .append(enPassantSquare)
                .append(" ")
                .append(halfmoveClock)
                .append(" ")
                .append(fullmoveNumber);

        return sb.toString();
    }



    // getters/setters for board and metadata
}

