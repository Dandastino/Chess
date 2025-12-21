package dandastino.chess.gameStates;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.games.Game;
import dandastino.chess.games.GamesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GameStateService {

    @Autowired
    private GameStatesRepository gameStatesRepository;

    @Autowired
    private GamesRepository gamesRepository;

    public List<GameStateResponseDTO> getAllGameStates() {
        return gameStatesRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public GameStateResponseDTO getGameStateById(UUID gameStateId) {
        GameState gameState = gameStatesRepository.findById(gameStateId)
                .orElseThrow(() -> new NotFoundException(gameStateId));
        return convertToDTO(gameState);
    }

    public GameStateResponseDTO createGameState(GameStateDTO gameStateDTO) {
        Game game = gamesRepository.findById(gameStateDTO.gameId())
                .orElseThrow(() -> new NotFoundException("Game not found"));

        GameState gameState = new GameState();
        gameState.setGame(game);
        gameState.setMove_number(gameStateDTO.moveNumber());
        gameState.setFen(gameStateDTO.fen());
        gameState.setEvaluation_cp(gameStateDTO.evaluationCp());
        gameState.setBest_move(gameStateDTO.bestMove());
        gameState.setAnalysis_json(gameStateDTO.analysisJson());

        GameState saved = gameStatesRepository.save(gameState);
        return convertToDTO(saved);
    }

    public GameStateResponseDTO updateGameState(UUID gameStateId, GameStateDTO gameStateDTO) {
        GameState gameState = gameStatesRepository.findById(gameStateId)
                .orElseThrow(() -> new NotFoundException(gameStateId));

        if (gameStateDTO.moveNumber() != null) {
            gameState.setMove_number(gameStateDTO.moveNumber());
        }
        if (gameStateDTO.fen() != null) {
            gameState.setFen(gameStateDTO.fen());
        }
        gameState.setEvaluation_cp(gameStateDTO.evaluationCp());
        if (gameStateDTO.bestMove() != null) {
            gameState.setBest_move(gameStateDTO.bestMove());
        }
        if (gameStateDTO.analysisJson() != null) {
            gameState.setAnalysis_json(gameStateDTO.analysisJson());
        }

        GameState saved = gameStatesRepository.save(gameState);
        return convertToDTO(saved);
    }

    public void deleteGameState(UUID gameStateId) {
        GameState gameState = gameStatesRepository.findById(gameStateId)
                .orElseThrow(() -> new NotFoundException(gameStateId));
        gameStatesRepository.delete(gameState);
    }

    public List<GameStateResponseDTO> getGameStatesByGame(UUID gameId) {
        return gameStatesRepository.findByGameId(gameId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private GameStateResponseDTO convertToDTO(GameState gameState) {
        return new GameStateResponseDTO(
                gameState.getGame_id(),
                gameState.getGame() != null ? gameState.getGame().getGame_id() : null,
                gameState.getMove_number(),
                gameState.getFen(),
                gameState.getEvaluation_cp(),
                gameState.getBest_move(),
                gameState.getAnalysis_json()
        );
    }
}

