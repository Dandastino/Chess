package dandastino.chess.openings;

import jakarta.validation.constraints.NotBlank;

public record OpeningDTO(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "ECO code is required")
        String ecoCode,
        String fenStart,
        String moves
) {}

