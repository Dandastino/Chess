package dandastino.chess.userSettings;

public record UserSettingDTO(
        String theme,
        String preferredTimeControl,
        String language,
        boolean allowEngineAnalysis,
        boolean showMoveSuggestions,
        boolean notificationsEnabled
) {}

