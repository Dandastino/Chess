package dandastino.chess.friends;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FriendDTO(
        @NotNull(message = "User 1 ID is required")
        UUID user1Id,
        @NotNull(message = "User 2 ID is required")
        UUID user2Id
) {}

