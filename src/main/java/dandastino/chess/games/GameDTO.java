package dandastino.chess.games;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record GameDTO(
        @JsonProperty("whitePlayerId")
        UUID whitePlayerId,
        @JsonProperty("blackPlayerId")
        UUID blackPlayerId,
        @JsonProperty("timeControl")
        String timeControl,
        @JsonProperty("initialFen")
        String initialFen,
        @JsonProperty(value = "isBotGame", defaultValue = "false")
        Boolean isBotGame,
        @JsonProperty(value = "botDifficulty", defaultValue = "0")
        Integer botDifficulty
) {
    public GameDTO {
        if (isBotGame == null) isBotGame = false;
        if (botDifficulty == null) botDifficulty = 0;
        
        // At least one player must be specified
        if (whitePlayerId == null && blackPlayerId == null) {
            throw new IllegalArgumentException("At least one player (white or black) must be specified");
        }
    }
}

