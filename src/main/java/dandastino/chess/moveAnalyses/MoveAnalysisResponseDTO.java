package dandastino.chess.moveAnalyses;

import java.util.UUID;

public record MoveAnalysisResponseDTO(
        UUID moveAnalysisId,
        UUID moveId,
        String evaluationCp,
        String bestMove,
        int depth,
        Review review
) {}

