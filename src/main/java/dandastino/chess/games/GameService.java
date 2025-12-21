package dandastino.chess.games;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersRepository;
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

    public GameResponseDTO createGame(GameDTO gameDTO) {
        User whitePlayer = usersRepository.findById(gameDTO.whitePlayerId())
                .orElseThrow(() -> new NotFoundException("White player not found"));
        User blackPlayer = usersRepository.findById(gameDTO.blackPlayerId())
                .orElseThrow(() -> new NotFoundException("Black player not found"));

        Game game = new Game();
        game.setStatus(Status.in_progress);
        game.setCreatedAt(LocalDateTime.now());
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setTimeControl(gameDTO.timeControl());
        game.setInitialFen(gameDTO.initialFen() != null ? gameDTO.initialFen() : "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        game.setIsBotGame(gameDTO.isBotGame());
        game.setBotDifficulty(gameDTO.botDifficulty());

        Game saved = gamesRepository.save(game);
        return convertToDTO(saved);
    }

    public GameResponseDTO updateGame(UUID gameId, GameDTO gameDTO) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));

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

    public GameResponseDTO finishGame(UUID gameId, Result result, UUID winnerId, String finalFen) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));

        game.setStatus(Status.done);
        game.setFinishedAt(LocalDateTime.now());
        game.setResult(result);
        game.setFinalFen(finalFen);

        if (winnerId != null) {
            User winner = usersRepository.findById(winnerId)
                    .orElseThrow(() -> new NotFoundException("Winner not found"));
            game.setWinner(winner);
        }

        Game saved = gamesRepository.save(game);
        return convertToDTO(saved);
    }

    public void deleteGame(UUID gameId) {
        Game game = gamesRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException(gameId));
        gamesRepository.delete(game);
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

    private GameResponseDTO convertToDTO(Game game) {
        return new GameResponseDTO(
                game.getGame_id(),
                game.getWhitePlayer().getUser_id(),
                game.getBlackPlayer().getUser_id(),
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
