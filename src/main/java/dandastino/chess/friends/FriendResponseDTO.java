package dandastino.chess.friends;

import java.time.LocalDateTime;
import java.util.UUID;

public record FriendResponseDTO(
        UUID friendshipId,
        UUID user1Id,
        UUID user2Id,
        LocalDateTime createdAt
) {}

