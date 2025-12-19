package dandastino.chess.gameLogic;

import dandastino.chess.moves.Move;

public class BoardUtils {


    /**
     * Converts 0-indexed (row, col) coordinates to algebraic chess notation (e.g., "e4").
     * @param row
     * @param col
     * @return the chess annotation
     */
    public static String toChessNotation(int row, int col) {
        // 0 -> 'a', 7 -> 'h'
        char file = (char) ('a' + col);
        // 7 -> '1', 0 -> '8'
        int rank = 8 - row;
        return "" + file + rank;
    }

    /**
     * Converts algebraic chess notation (e.g., "e4") to 0-indexed (row, col) coordinates.
     * @param notation
     * @return the coordinates
     */
    public static int[] toCoordinates(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid chess notation format: " + notation);
        }

        char fileChar = notation.toLowerCase().charAt(0);
        char rankChar = notation.charAt(1);

        // File ('a' through 'h') to Column (0 through 7)
        int col = fileChar - 'a';

        // Rank ('1' through '8') to Row (7 through 0)
        int rank = Character.getNumericValue(rankChar);
        int row = 8 - rank;

        if (col < 0 || col > 7 || row < 0 || row > 7) {
            throw new IllegalArgumentException("Coordinates out of bounds: " + notation);
        }

        // Returns an array {row, col}
        return new int[]{row, col};
    }

    /**
     * Creates a Move object from algebraic chess notation (e.g., "e4").
     * @param from
     * @param to
     * @return the Move object
     */
    public static Move toMoveObject(String from, String to) {
        // 1. Convert 'from' notation (e.g., "e2") to coordinates {row, col}
        int[] startCoords = toCoordinates(from);
        int startRow = startCoords[0];
        int startCol = startCoords[1];

        // 2. Convert 'to' notation (e.g., "e4") to coordinates {row, col}
        int[] endCoords = toCoordinates(to);
        int endRow = endCoords[0];
        int endCol = endCoords[1];

        // 3. Create and return the Move object
        return new Move(startRow, startCol, endRow, endCol);
    }
}