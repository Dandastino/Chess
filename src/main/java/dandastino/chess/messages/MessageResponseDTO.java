package dandastino.chess.messages;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponseDTO(
        UUID messageId,
        UUID gameId,
        UUID userId,
        String content,
        LocalDateTime timestamp
) {}

