package dandastino.chess.messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageDTO(
        @NotNull(message = "Game ID is required")
        UUID gameId,
        @NotNull(message = "User ID is required")
        UUID userId,
        @NotBlank(message = "Content is required")
        String content
) {}

