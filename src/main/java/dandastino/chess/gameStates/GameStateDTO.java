package dandastino.chess.gameStates;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GameStateDTO(
        @NotNull(message = "Game ID is required")
        UUID gameId,
        String moveNumber,
        String fen,
        int evaluationCp,
        String bestMove,
        String analysisJson
) {}

