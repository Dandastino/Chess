package dandastino.chess.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameMoveMessage {
    @JsonProperty("game_id")
    private String gameId;
    
    @JsonProperty("move")
    private String move;
    
    @JsonProperty("san_move")
    private String sanMove;
    
    @JsonProperty("fen")
    private String fen;
    
    @JsonProperty("is_check")
    private boolean isCheck;
    
    @JsonProperty("is_checkmate")
    private boolean isCheckmate;
    
    @JsonProperty("is_stalemate")
    private boolean isStalemate;
    
    @JsonProperty("player_id")
    private String playerId;
    
    @JsonProperty("timestamp")
    private long timestamp;

    public GameMoveMessage() {}

    public GameMoveMessage(String gameId, String move, String sanMove, String fen, 
                          boolean isCheck, boolean isCheckmate, boolean isStalemate, 
                          String playerId, long timestamp) {
        this.gameId = gameId;
        this.move = move;
        this.sanMove = sanMove;
        this.fen = fen;
        this.isCheck = isCheck;
        this.isCheckmate = isCheckmate;
        this.isStalemate = isStalemate;
        this.playerId = playerId;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getSanMove() {
        return sanMove;
    }

    public void setSanMove(String sanMove) {
        this.sanMove = sanMove;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean checkmate) {
        isCheckmate = checkmate;
    }

    public boolean isStalemate() {
        return isStalemate;
    }

    public void setStalemate(boolean stalemate) {
        isStalemate = stalemate;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
