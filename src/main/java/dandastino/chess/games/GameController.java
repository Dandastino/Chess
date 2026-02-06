package dandastino.chess.games;

import dandastino.chess.exceptions.ValidationException;
import dandastino.chess.users.User;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public GameResponseDTO createGame(@RequestBody @Validated GameDTO body, @AuthenticationPrincipal User player, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return gameService.createGame(body, player);
    }

    @PutMapping("/{game_id}")
    public GameResponseDTO updateGame(@PathVariable("game_id") UUID gameId, @RequestBody @Validated GameDTO body, @AuthenticationPrincipal User player) {
        return gameService.updateGame(gameId, body, player);
    }

    @PatchMapping("/{game_id}/finish")
    public GameResponseDTO finishGame(
            @PathVariable("game_id") UUID gameId,
            @RequestParam(value = "result", required = false) String resultStr,
            @RequestParam(value = "winner_id", required = false) UUID winnerId,
            @RequestParam(value = "final_fen", required = false) String finalFen,
            @AuthenticationPrincipal User player) {
        Result result = resultStr != null ? Result.valueOf(resultStr) : null;
        return gameService.finishGame(gameId, result, winnerId, finalFen, player);
    }

    @DeleteMapping("/{game_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable("game_id") UUID gameId, @AuthenticationPrincipal User player) {
        gameService.deleteGame(gameId, player);
    }

    @GetMapping("/player/{player_id}")
    public List<GameResponseDTO> getGamesByPlayer(@PathVariable("player_id") UUID playerId) {
        return gameService.getGamesByPlayer(playerId);
    }

    @GetMapping("/status/{status}")
    public List<GameResponseDTO> getGamesByStatus(@PathVariable("status") Status status) {
        return gameService.getGamesByStatus(status);
    }

    @GetMapping("/available")
    public List<GameResponseDTO> getAvailableGames(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "time_control", required = false) String timeControl) {
        return gameService.getAvailableGames(limit, timeControl);
    }

    @PatchMapping("/{game_id}/resign")
    public GameResponseDTO resignGame(
            @PathVariable("game_id") UUID gameId,
            @AuthenticationPrincipal User player) {
        return gameService.resignGame(gameId, player);
    }

    @PostMapping("/{game_id}/join")
    public GameResponseDTO joinGame(
            @PathVariable("game_id") UUID gameId,
            @AuthenticationPrincipal User player) {
        return gameService.joinGame(gameId, player);
    }

    @PatchMapping("/{game_id}/draw")
    public GameResponseDTO proposeDrawGame(
            @PathVariable("game_id") UUID gameId,
            @RequestParam("accept") boolean accept,
            @AuthenticationPrincipal User player) {
        return gameService.handleDrawProposal(gameId, accept, player);
    }
}
