package dandastino.chess.games;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.exceptions.ForbiddenException;
import dandastino.chess.exceptions.ConflictException;
import dandastino.chess.exceptions.ValidationException;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersRepository;
import dandastino.chess.websocket.GameBroadcastService;
import dandastino.chess.gameStates.GameStateService;
import dandastino.chess.gameStates.GameStateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private GameBroadcastService broadcastService;

    @Autowired
    private GameStateService gameStateService;

    public List<GameResponseDTO> getAllGames() {
        return gamesRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public GameResponseDTO getGameById(UUID gameId) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));
        return convertToDTO(game);
    }

    public GameResponseDTO createGame(GameDTO gameDTO, User author) {
        User whitePlayer = null;
        User blackPlayer = null;
        
        if (gameDTO.whitePlayerId() != null) {
            whitePlayer = usersRepository.findById(gameDTO.whitePlayerId())
                    .orElseThrow(() -> new NotFoundException("White player not found"));
        }
        
        if (gameDTO.blackPlayerId() != null) {
            blackPlayer = usersRepository.findById(gameDTO.blackPlayerId())
                    .orElseThrow(() -> new NotFoundException("Black player not found"));
        }

        Game game = new Game();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setTimeControl(gameDTO.timeControl());
        game.setInitialFen(gameDTO.initialFen() != null ? gameDTO.initialFen() : "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        game.setIsBotGame(gameDTO.isBotGame());
        game.setBotDifficulty(gameDTO.botDifficulty());
        
        // Set status based on player availability
        if (whitePlayer != null && blackPlayer != null) {
            game.setStatus(Status.in_progress);
        } else {
            game.setStatus(Status.waiting_for_player);
        }

        Game saved = gamesRepository.save(game);
        
        return convertToDTO(saved);
    }

    public GameResponseDTO updateGame(UUID gameId, GameDTO gameDTO, User player) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));
        ensureParticipant(game, player);

        if (gameDTO.whitePlayerId() != null) {
            User whitePlayer = usersRepository.findById(gameDTO.whitePlayerId())
                    .orElseThrow(() -> new NotFoundException("White player not found"));
            game.setWhitePlayer(whitePlayer);
        }

        if (gameDTO.blackPlayerId() != null) {
            User blackPlayer = usersRepository.findById(gameDTO.blackPlayerId())
                    .orElseThrow(() -> new NotFoundException("Black player not found"));
            game.setBlackPlayer(blackPlayer);
        }

        if (gameDTO.timeControl() != null) {
            game.setTimeControl(gameDTO.timeControl());
        }

        if (gameDTO.initialFen() != null) {
            game.setInitialFen(gameDTO.initialFen());
        }

        game.setIsBotGame(gameDTO.isBotGame());
        game.setBotDifficulty(gameDTO.botDifficulty());

        Game saved = gamesRepository.save(game);
        return convertToDTO(saved);
    }

    public GameResponseDTO finishGame(UUID gameId, Result result, UUID winnerId, String finalFen, User player) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));
        ensureParticipant(game, player);

        game.setStatus(Status.done);
        game.setFinishedAt(LocalDateTime.now());
        game.setFinalFen(finalFen);

        // Auto-determine result and winner
        Result finalResult;
        if (winnerId != null) {
            // Winner ID provided - set winner and determine result
            User winner = usersRepository.findById(winnerId)
                    .orElseThrow(() -> new NotFoundException("Winner not found"));
            game.setWinner(winner);
            
            // Auto-set result based on which player is the winner
            if (game.getWhitePlayer() != null && winner.getId().equals(game.getWhitePlayer().getId())) {
                finalResult = Result.white_wins;
            } else if (game.getBlackPlayer() != null && winner.getId().equals(game.getBlackPlayer().getId())) {
                finalResult = Result.black_wins;
            } else {
                throw new ValidationException("Winner is not a participant in this game");
            }
        } else if (result != null) {
            // Result provided but no winner_id - auto-determine winner from result
            finalResult = result;
            if (result == Result.white_wins) {
                game.setWinner(game.getWhitePlayer());
            } else if (result == Result.black_wins) {
                game.setWinner(game.getBlackPlayer());
            } else {
                game.setWinner(null); // Draw has no winner
            }
        } else {
            // Nothing provided - default to draw
            finalResult = Result.draw;
            game.setWinner(null);
        }
        
        game.setResult(finalResult);

        Game saved = gamesRepository.save(game);
        GameResponseDTO responseDTO = convertToDTO(saved);
        
        // Create final GameState snapshot at game end
        if (finalFen != null) {
            GameStateDTO finalState = new GameStateDTO(
                    saved.getGame_id(),
                    "200",  // placeholder for final move number (can be updated if move count is tracked)
                    finalFen,
                    0,
                    null,
                    null
            );
            gameStateService.createGameState(finalState);
        }
        
        // Broadcast the game finish event to all connected players via WebSocket
        broadcastService.broadcastGameFinished(responseDTO, finalResult);
        
        return responseDTO;
    }

    public void deleteGame(UUID gameId, User player) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));
        ensureParticipant(game, player);
        gamesRepository.delete(game);
    }

    private void ensureParticipant(Game game, User player) {
        UUID playerId = player.getId();
        boolean isWhite = game.getWhitePlayer() != null && game.getWhitePlayer().getId().equals(playerId);
        boolean isBlack = game.getBlackPlayer() != null && game.getBlackPlayer().getId().equals(playerId);
        if (!isWhite && !isBlack) {
            throw new ForbiddenException("Only game participants can perform this action");
        }
    }

    public List<GameResponseDTO> getGamesByPlayer(UUID playerId) {
        return gamesRepository.findByWhitePlayerIdOrBlackPlayerId(playerId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<GameResponseDTO> getGamesByStatus(Status status) {
        return gamesRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<GameResponseDTO> getAvailableGames(int limit, String timeControl) {
        return gamesRepository.findAll().stream()
                .filter(g -> g.getStatus() == Status.in_progress)
                .filter(g -> g.getBlackPlayer() == null || g.getWhitePlayer() == null)
                .filter(g -> timeControl == null || g.getTime_control().equals(timeControl))
                .limit(limit)
                .map(this::convertToDTO)
                .toList();
    }

    public GameResponseDTO resignGame(UUID gameId, User player) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));
        ensureParticipant(game, player);

        game.setStatus(Status.done);
        game.setFinishedAt(LocalDateTime.now());

        // Determine winner based on who resigned
        if (game.getWhitePlayer() != null && game.getWhitePlayer().getId().equals(player.getId())) {
            game.setWinner(game.getBlackPlayer());
            game.setResult(Result.black_wins);
        } else if (game.getBlackPlayer() != null && game.getBlackPlayer().getId().equals(player.getId())) {
            game.setWinner(game.getWhitePlayer());
            game.setResult(Result.white_wins);
        } else {
            game.setResult(Result.draw);
        }

        Game saved = gamesRepository.save(game);
        GameResponseDTO responseDTO = convertToDTO(saved);
        
        // Create final GameState snapshot on resignation
        if (game.getFinalFen() != null) {
            GameStateDTO finalState = new GameStateDTO(
                    saved.getGame_id(),
                    "200",  // placeholder for final move number
                    game.getFinalFen(),
                    0,
                    null,
                    null
            );
            gameStateService.createGameState(finalState);
        }
        
        broadcastService.broadcastGameFinished(responseDTO, game.getResult());
        return responseDTO;
    }

    public GameResponseDTO joinGame(UUID gameId, User player) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));

        if (game.getBlackPlayer() == null) {
            game.setBlackPlayer(player);
        } else if (game.getWhitePlayer() == null) {
            game.setWhitePlayer(player);
        } else {
            throw new ConflictException("Game is already full");
        }

        if (game.getWhitePlayer() != null && game.getBlackPlayer() != null) {
            game.setStatus(Status.in_progress);
        }

        Game saved = gamesRepository.save(game);
        return convertToDTO(saved);
    }

    public GameResponseDTO handleDrawProposal(UUID gameId, boolean accept, User player) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));
        ensureParticipant(game, player);

        if (accept) {
            game.setStatus(Status.done);
            game.setFinishedAt(LocalDateTime.now());
            game.setResult(Result.draw);
            Game saved = gamesRepository.save(game);
            GameResponseDTO responseDTO = convertToDTO(saved);
            
            // Create final GameState snapshot on draw acceptance
            if (game.getFinalFen() != null) {
                GameStateDTO finalState = new GameStateDTO(
                        saved.getGame_id(),
                        "200",  // placeholder for final move number
                        game.getFinalFen(),
                        0,
                        null,
                        null
                );
                gameStateService.createGameState(finalState);
            }
            
            broadcastService.broadcastGameFinished(responseDTO, Result.draw);
            return responseDTO;
        }

        // If not accepting, just return current game state
        return convertToDTO(game);
    }

    private GameResponseDTO convertToDTO(Game game) {
        return new GameResponseDTO(
                game.getGame_id(),
                game.getWhitePlayer() != null ? game.getWhitePlayer().getUser_id() : null,
                game.getBlackPlayer() != null ? game.getBlackPlayer().getUser_id() : null,
                game.getStatus(),
                game.getCreatedAt(),
                game.getFinishedAt(),
                game.getResult(),
                game.getTime_control(),
                game.getInitialFen(),
                game.getFinalFen(),
                game.getIsBotGame(),
                game.getBotDifficulty(),
                game.getWinner() != null ? game.getWinner().getUser_id() : null
        );
    }
}
