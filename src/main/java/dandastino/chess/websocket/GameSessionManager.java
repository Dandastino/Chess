package dandastino.chess.websocket;

import org.springframework.web.socket.WebSocketSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages WebSocket sessions for active chess games.
 * Tracks which players are connected to which games.
 */
public class GameSessionManager {
    
    private static final Map<String, Set<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();
    private static final Map<String, String> playerToGame = new ConcurrentHashMap<>();

    /**
     * Register a player's WebSocket session for a specific game
     */
    public static void registerSession(String gameId, String playerId, WebSocketSession session) {
        gameSessions.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet()).add(session);
        playerToGame.put(playerId, gameId);
    }

    /**
     * Unregister a player's WebSocket session
     */
    public static void unregisterSession(String playerId, WebSocketSession session) {
        String gameId = playerToGame.remove(playerId);
        if (gameId != null) {
            Set<WebSocketSession> sessions = gameSessions.get(gameId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    gameSessions.remove(gameId);
                }
            }
        }
    }

    /**
     * Get all active sessions for a specific game
     */
    public static Set<WebSocketSession> getGameSessions(String gameId) {
        return gameSessions.getOrDefault(gameId, Collections.emptySet());
    }

    /**
     * Check if a game has active sessions
     */
    public static boolean isGameActive(String gameId) {
        return gameSessions.containsKey(gameId) && !gameSessions.get(gameId).isEmpty();
    }

    /**
     * Get the game ID for a specific player
     */
    public static String getGameForPlayer(String playerId) {
        return playerToGame.get(playerId);
    }

    /**
     * Get the number of active players in a game
     */
    public static int getActivePlayersCount(String gameId) {
        Set<WebSocketSession> sessions = gameSessions.get(gameId);
        return sessions != null ? sessions.size() : 0;
    }
}
