package dandastino.chess.moves;

public record MoveDTO(
        String oldFen,
        String newFen,
        boolean isLegal,
        boolean isCheck,
        boolean isCheckmate,
        boolean isStalemate,
        boolean isDraw,
        String sanMove,    // e.g., "Nf3", "Qxd5"
        String from,
        String to) {
}
