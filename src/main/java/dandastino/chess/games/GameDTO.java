package dandastino.chess.games;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record GameDTO(
        @NotNull(message = "White player ID is required")
        UUID whitePlayerId,
        @NotNull(message = "Black player ID is required")
        UUID blackPlayerId,
        String timeControl,
        String initialFen,
        boolean isBotGame,
        int botDifficulty
) {}

