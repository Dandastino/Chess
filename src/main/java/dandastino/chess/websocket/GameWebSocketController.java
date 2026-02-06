package dandastino.chess.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class GameWebSocketController {
    
    private static final Logger logger = LoggerFactory.getLogger(GameWebSocketController.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Handles incoming move messages from clients and broadcasts them to all subscribers
     * of that game's topic.
     * 
     * Client sends to: /app/game/{gameId}/move
     * Server broadcasts to: /topic/game/{gameId}/move
     */
    @MessageMapping("/game/{gameId}/move")
    public void handleGameMove(@DestinationVariable String gameId, 
                               @Payload GameMoveMessage moveMessage) {
        logger.info("Received move in game {}: {}", gameId, moveMessage.getSanMove());
        
        moveMessage.setGameId(gameId);
        moveMessage.setTimestamp(System.currentTimeMillis());
        
        // Broadcast the move to all subscribers of this game
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/move", moveMessage);
    }

    /**
     * Handles game status updates (e.g., game finished, player resigned).
     * 
     * Client sends to: /app/game/{gameId}/status
     * Server broadcasts to: /topic/game/{gameId}/status
     */
    @MessageMapping("/game/{gameId}/status")
    public void handleGameStatus(@DestinationVariable String gameId,
                                 @Payload GameStatusMessage statusMessage) {
        logger.info("Received status update in game {}: {}", gameId, statusMessage.getStatus());
        
        statusMessage.setGameId(gameId);
        statusMessage.setTimestamp(System.currentTimeMillis());
        
        // Broadcast the status update to all subscribers of this game
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/status", statusMessage);
    }

    /**
     * Handles player connection notifications.
     * 
     * Client sends to: /app/game/{gameId}/player-joined
     * Server broadcasts to: /topic/game/{gameId}/players
     */
    @MessageMapping("/game/{gameId}/player-joined")
    public void handlePlayerJoined(@DestinationVariable String gameId,
                                   @Payload String playerId) {
        logger.info("Player {} joined game {}", playerId, gameId);
        
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/players", 
                                        "Player " + playerId + " joined the game");
    }

    /**
     * Handles player disconnection notifications.
     * 
     * Client sends to: /app/game/{gameId}/player-left
     * Server broadcasts to: /topic/game/{gameId}/players
     */
    @MessageMapping("/game/{gameId}/player-left")
    public void handlePlayerLeft(@DestinationVariable String gameId,
                                 @Payload String playerId) {
        logger.info("Player {} left game {}", playerId, gameId);
        
        messagingTemplate.convertAndSend("/topic/game/" + gameId + "/players", 
                                        "Player " + playerId + " left the game");
    }
}
