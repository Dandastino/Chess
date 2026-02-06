package dandastino.chess.gamesOpenings;

import java.util.UUID;

public record GameOpeningResponseDTO(
        UUID gameOpeningId,
        UUID gameId,
        UUID openingId
) {}

