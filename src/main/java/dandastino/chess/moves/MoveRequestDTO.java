package dandastino.chess.moves;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MoveRequestDTO(
        @NotNull(message = "Game ID is required")
        UUID gameId,
        @NotNull(message = "Player ID is required")
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
        int timeSpentMs,
        @JsonProperty("is_check")
        boolean isCheck,
        @JsonProperty("is_checkmate")
        boolean isCheckmate
) {}

