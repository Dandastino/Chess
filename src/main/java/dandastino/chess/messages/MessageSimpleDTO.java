package dandastino.chess.messages;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageSimpleDTO(
        String content,
        LocalDateTime timestamp,
        UUID gameId
) {}
