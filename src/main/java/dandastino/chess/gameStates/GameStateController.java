package dandastino.chess.gameStates;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game-states")
public class GameStateController {

    private final GameStateService gameStateService;

    public GameStateController(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    @GetMapping
    public List<GameStateResponseDTO> getAllGameStates() {
        return gameStateService.getAllGameStates();
    }

    @GetMapping("/{game_state_id}")
    public GameStateResponseDTO getGameStateById(@PathVariable("game_state_id") UUID gameStateId) {
        return gameStateService.getGameStateById(gameStateId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameStateResponseDTO createGameState(@RequestBody @Validated GameStateDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return gameStateService.createGameState(body);
    }

    @PutMapping("/{game_state_id}")
    public GameStateResponseDTO updateGameState(@PathVariable("game_state_id") UUID gameStateId, @RequestBody @Validated GameStateDTO body) {
        return gameStateService.updateGameState(gameStateId, body);
    }

    @DeleteMapping("/{game_state_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGameState(@PathVariable("game_state_id") UUID gameStateId) {
        gameStateService.deleteGameState(gameStateId);
    }

    @GetMapping("/game/{game_id}")
    public List<GameStateResponseDTO> getGameStatesByGame(@PathVariable("game_id") UUID gameId) {
        return gameStateService.getGameStatesByGame(gameId);
    }
}

