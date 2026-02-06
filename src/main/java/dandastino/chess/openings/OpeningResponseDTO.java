package dandastino.chess.openings;

import java.util.UUID;

public record OpeningResponseDTO(
        UUID openingId,
        String name,
        String ecoCode,
        String fenStart,
        String moves
) {}

