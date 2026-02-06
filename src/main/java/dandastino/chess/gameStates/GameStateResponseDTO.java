package dandastino.chess.gameStates;

import java.util.UUID;

public record GameStateResponseDTO(
        UUID gameStateId,
        UUID gameId,
        String moveNumber,
        String fen,
        int evaluationCp,
        String bestMove,
        String analysisJson
) {}

