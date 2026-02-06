package dandastino.chess.games;

import java.time.LocalDateTime;
import java.util.UUID;

public record GameResponseDTO(
        UUID gameId,
        UUID whitePlayerId,
        UUID blackPlayerId,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime finishedAt,
        Result result,
        String timeControl,
        String initialFen,
        String finalFen,
        boolean isBotGame,
        int botDifficulty,
        UUID winnerId
) {}

