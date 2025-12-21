package dandastino.chess.moveAnalyses;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MoveAnalysisDTO(
        @NotNull(message = "Move ID is required")
        UUID moveId,
        String evaluationCp,
        String bestMove,
        int depth,
        Review review
) {}

