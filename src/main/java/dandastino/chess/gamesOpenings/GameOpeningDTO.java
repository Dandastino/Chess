package dandastino.chess.gamesOpenings;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GameOpeningDTO(
        @NotNull(message = "Game ID is required")
        UUID gameId,
        @NotNull(message = "Opening ID is required")
        UUID openingId
) {}

