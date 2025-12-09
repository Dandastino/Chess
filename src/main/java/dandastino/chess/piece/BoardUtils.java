package dandastino.chess.piece;

public class BoardUtils {
    public static String toChessNotation(int row, int col) {
        // 0 -> 'a', 7 -> 'h'
        char file = (char) ('a' + col);
        // 7 -> '1', 0 -> '8'
        int rank = 8 - row;
        return "" + file + rank;
    }
}