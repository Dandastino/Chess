package dandastino.chess.websocket;

import dandastino.chess.moves.MoveResponseDTO;
import dandastino.chess.games.GameResponseDTO;
import dandastino.chess.games.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Service to integrate REST API operations with WebSocket real-time updates.
 * This service broadcasts game events to all connected players via WebSocket.
 */
@Service
public class GameBroadcastService {
    
    private static final Logger logger = LoggerFactory.getLogger(GameBroadcastService.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast a new move to all players in a game
     */
    public void broadcastMove(MoveResponseDTO moveDTO) {
        try {
            String gameId = moveDTO.gameId().toString();
            
            GameMoveMessage moveMessage = new GameMoveMessage(
                gameId,
                moveDTO.fromSquare() + moveDTO.toSquare(),
                moveDTO.sanMove(),
                moveDTO.fenAfterMove(),
                moveDTO.isCheck(),
                moveDTO.isCheckmate(),
                false, // isStalemate - would need to be passed separately
                moveDTO.playerId().toString(),
                System.currentTimeMillis()
            );
            
            messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/move",
                moveMessage
            );
            
            logger.info("Broadcasted move {} in game {}", moveDTO.sanMove(), gameId);
        } catch (Exception e) {
            logger.error("Error broadcasting move", e);
        }
    }

    /**
     * Broadcast game finished event
     */
    public void broadcastGameFinished(GameResponseDTO gameDTO, Result result) {
        try {
            String gameId = gameDTO.gameId().toString();
            
            String message = determineGameEndMessage(result);
            
            GameStatusMessage statusMessage = new GameStatusMessage(
                gameId,
                gameDTO.status().toString(),
                result.toString(),
                gameDTO.winnerId() != null ? gameDTO.winnerId().toString() : null,
                message,
                System.currentTimeMillis()
            );
            
            messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/status",
                statusMessage
            );
            
            logger.info("Broadcasted game finish event for game {} with result: {}", gameId, result);
        } catch (Exception e) {
            logger.error("Error broadcasting game finish event", e);
        }
    }

    /**
     * Broadcast player resignation
     */
    public void broadcastPlayerResigned(String gameId, UUID playerId) {
        try {
            GameStatusMessage statusMessage = new GameStatusMessage(
                gameId,
                "done",
                "resignation",
                null,
                "Player " + playerId + " resigned",
                System.currentTimeMillis()
            );
            
            messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/status",
                statusMessage
            );
            
            logger.info("Broadcasted player resignation in game {}", gameId);
        } catch (Exception e) {
            logger.error("Error broadcasting player resignation", e);
        }
    }

    /**
     * Broadcast draw offer
     */
    public void broadcastDrawOffer(String gameId, UUID playerId) {
        try {
            GameStatusMessage statusMessage = new GameStatusMessage(
                gameId,
                "in_progress",
                "draw_offered",
                null,
                "Player " + playerId + " offered a draw",
                System.currentTimeMillis()
            );
            
            messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/status",
                statusMessage
            );
            
            logger.info("Broadcasted draw offer in game {}", gameId);
        } catch (Exception e) {
            logger.error("Error broadcasting draw offer", e);
        }
    }

    /**
     * Broadcast time out event
     */
    public void broadcastTimeOut(String gameId, UUID playerId) {
        try {
            GameStatusMessage statusMessage = new GameStatusMessage(
                gameId,
                "done",
                "timeout",
                null,
                "Player " + playerId + " ran out of time",
                System.currentTimeMillis()
            );
            
            messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/status",
                statusMessage
            );
            
            logger.info("Broadcasted timeout event in game {}", gameId);
        } catch (Exception e) {
            logger.error("Error broadcasting timeout event", e);
        }
    }

    private String determineGameEndMessage(Result result) {
        return switch (result) {
            case draw -> "Game is a draw.";
            case white_wins -> "White wins!";
            case black_wins -> "Black wins!";
            default -> "Game finished.";
        };
    }
}
