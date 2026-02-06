package dandastino.chess.cheatingAnalyses;

import java.time.LocalDateTime;
import java.util.UUID;

public record CheatingAnalysisResponseDTO(
        UUID cheatingAnalysisId,
        UUID gameId,
        UUID userId,
        double matchAccuracyPerc,
        double suspicionScore,
        LocalDateTime createdAt
) {}

