package dandastino.chess.gamesOpenings;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game-openings")
public class GameOpeningController {

    private final GameOpeningService gameOpeningService;

    public GameOpeningController(GameOpeningService gameOpeningService) {
        this.gameOpeningService = gameOpeningService;
    }

    @GetMapping
    public List<GameOpeningResponseDTO> getAllGameOpenings() {
        return gameOpeningService.getAllGameOpenings();
    }

    @GetMapping("/{game_opening_id}")
    public GameOpeningResponseDTO getGameOpeningById(@PathVariable("game_opening_id") UUID gameOpeningId) {
        return gameOpeningService.getGameOpeningById(gameOpeningId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameOpeningResponseDTO createGameOpening(@RequestBody @Validated GameOpeningDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return gameOpeningService.createGameOpening(body);
    }

    @DeleteMapping("/{game_opening_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGameOpening(@PathVariable("game_opening_id") UUID gameOpeningId) {
        gameOpeningService.deleteGameOpening(gameOpeningId);
    }

    @GetMapping("/game/{game_id}")
    public List<GameOpeningResponseDTO> getGameOpeningsByGame(@PathVariable("game_id") UUID gameId) {
        return gameOpeningService.getGameOpeningsByGame(gameId);
    }

    @GetMapping("/opening/{opening_id}")
    public List<GameOpeningResponseDTO> getGameOpeningsByOpening(@PathVariable("opening_id") UUID openingId) {
        return gameOpeningService.getGameOpeningsByOpening(openingId);
    }
}

