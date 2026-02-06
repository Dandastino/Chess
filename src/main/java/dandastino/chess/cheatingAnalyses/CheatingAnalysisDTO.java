package dandastino.chess.cheatingAnalyses;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheatingAnalysisDTO(
        @NotNull(message = "Game ID is required")
        UUID gameId,
        @NotNull(message = "User ID is required")
        UUID userId,
        double matchAccuracyPerc,
        double suspicionScore
) {}

