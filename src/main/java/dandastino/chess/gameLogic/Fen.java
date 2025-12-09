package dandastino.chess.gameLogic;

public class Fen {
    private Board board;        // 8x8 board
    private boolean whiteToMove;
    private CastlingRights castlingRights;
    private EnPassantSquare enPassantSquare;
    private int halfmoveClock;
    private int fullmoveNumber;

    public Fen(String fen) {
        parseFen(fen);
    }

    private void parseFen(String fen) {
        String[] parts = fen.split(" ");

        // 1. Board
        Board board = new Board();
        String[] ranks = parts[0].split("/");

        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : ranks[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    int empty = Character.getNumericValue(c);
                    for (int i = 0; i < empty; i++) {
                        board[row][col++] = '.';
                    }
                } else {
                    board[row][col++] = c;
                }
            }
        }

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
                char piece = board[row][col];
                if (piece == '.') empty++;
                else {
                    if (empty > 0) {
                        sb.append(empty);
                        empty = 0;
                    }
                    sb.append(piece);
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

