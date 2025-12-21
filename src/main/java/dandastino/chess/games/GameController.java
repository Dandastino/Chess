package dandastino.chess.games;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public List<GameResponseDTO> getAllGames() {
        return gameService.getAllGames();
    }

    @GetMapping("/{game_id}")
    public GameResponseDTO getGameById(@PathVariable("game_id") UUID gameId) {
        return gameService.getGameById(gameId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponseDTO createGame(@RequestBody @Validated GameDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return gameService.createGame(body);
    }

    @PutMapping("/{game_id}")
    public GameResponseDTO updateGame(@PathVariable("game_id") UUID gameId, @RequestBody @Validated GameDTO body) {
        return gameService.updateGame(gameId, body);
    }

    @PatchMapping("/{game_id}/finish")
    public GameResponseDTO finishGame(
            @PathVariable("game_id") UUID gameId,
            @RequestParam("result") Result result,
            @RequestParam(value = "winner_id", required = false) UUID winnerId,
            @RequestParam(value = "final_fen", required = false) String finalFen) {
        return gameService.finishGame(gameId, result, winnerId, finalFen);
    }

    @DeleteMapping("/{game_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable("game_id") UUID gameId) {
        gameService.deleteGame(gameId);
    }

    @GetMapping("/player/{player_id}")
    public List<GameResponseDTO> getGamesByPlayer(@PathVariable("player_id") UUID playerId) {
        return gameService.getGamesByPlayer(playerId);
    }

    @GetMapping("/status/{status}")
    public List<GameResponseDTO> getGamesByStatus(@PathVariable("status") Status status) {
        return gameService.getGamesByStatus(status);
    }
}
