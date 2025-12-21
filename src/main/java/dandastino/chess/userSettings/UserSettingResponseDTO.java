package dandastino.chess.userSettings;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserSettingResponseDTO(
        UUID userSettingId,
        UUID userId,
        String theme,
        String preferredTimeControl,
        String language,
        boolean allowEngineAnalysis,
        boolean showMoveSuggestions,
        boolean notificationsEnabled,
        LocalDateTime createdAt
) {}

