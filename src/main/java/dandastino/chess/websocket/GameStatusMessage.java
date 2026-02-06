package dandastino.chess.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameStatusMessage {
    @JsonProperty("game_id")
    private String gameId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("result")
    private String result;
    
    @JsonProperty("winner_id")
    private String winnerId;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("timestamp")
    private long timestamp;

    public GameStatusMessage() {}

    public GameStatusMessage(String gameId, String status, String result, 
                            String winnerId, String message, long timestamp) {
        this.gameId = gameId;
        this.status = status;
        this.result = result;
        this.winnerId = winnerId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
