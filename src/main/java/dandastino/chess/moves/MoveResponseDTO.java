package dandastino.chess.moves;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record MoveResponseDTO(
        UUID moveId,
        UUID gameId,
        UUID playerId,
        int moveNumber,
        String sanMove,
        String fromSquare,
        String toSquare,
        int startRow,
        int startCol,
        int endRow,
        int endCol,
        String fenAfterMove,
        LocalDateTime timestamp,
        int timeSpentMs,
        @JsonProperty("is_check")
        boolean isCheck,
        @JsonProperty("is_checkmate")
        boolean isCheckmate
) {}

