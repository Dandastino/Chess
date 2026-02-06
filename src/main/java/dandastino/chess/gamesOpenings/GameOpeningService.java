package dandastino.chess.gamesOpenings;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.games.Game;
import dandastino.chess.games.GamesRepository;
import dandastino.chess.openings.Opening;
import dandastino.chess.openings.OpeningsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GameOpeningService {

    @Autowired
    private GameOpeningsRepository gameOpeningsRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private OpeningsRepository openingsRepository;

    public List<GameOpeningResponseDTO> getAllGameOpenings() {
        return gameOpeningsRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public GameOpeningResponseDTO getGameOpeningById(UUID gameOpeningId) {
        GameOpening gameOpening = gameOpeningsRepository.findById(gameOpeningId)
                .orElseThrow(() -> new NotFoundException(gameOpeningId));
        return convertToDTO(gameOpening);
    }

    public GameOpeningResponseDTO createGameOpening(GameOpeningDTO gameOpeningDTO) {
        Game game = gamesRepository.findById(gameOpeningDTO.gameId())
                .orElseThrow(() -> new NotFoundException("Game not found"));
        Opening opening = openingsRepository.findById(gameOpeningDTO.openingId())
                .orElseThrow(() -> new NotFoundException("Opening not found"));

        GameOpening gameOpening = new GameOpening(opening, game);
        GameOpening saved = gameOpeningsRepository.save(gameOpening);
        return convertToDTO(saved);
    }

    public void deleteGameOpening(UUID gameOpeningId) {
        GameOpening gameOpening = gameOpeningsRepository.findById(gameOpeningId)
                .orElseThrow(() -> new NotFoundException(gameOpeningId));
        gameOpeningsRepository.delete(gameOpening);
    }

    public List<GameOpeningResponseDTO> getGameOpeningsByGame(UUID gameId) {
        return gameOpeningsRepository.findByGameId(gameId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<GameOpeningResponseDTO> getGameOpeningsByOpening(UUID openingId) {
        return gameOpeningsRepository.findByOpeningId(openingId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private GameOpeningResponseDTO convertToDTO(GameOpening gameOpening) {
        return new GameOpeningResponseDTO(
                gameOpening.getGame_opening_id(),
                gameOpening.getGame() != null ? gameOpening.getGame().getGame_id() : null,
                gameOpening.getOpening() != null ? gameOpening.getOpening().getOpening_id() : null
        );
    }
}

