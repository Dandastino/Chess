package dandastino.chess.moves;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/moves")
public class MoveController {

    private final MoveService moveService;

    public MoveController(MoveService moveService) {
        this.moveService = moveService;
    }

    @GetMapping
    public List<MoveResponseDTO> getAllMoves() {
        return moveService.getAllMoves();
    }

    @GetMapping("/{move_id}")
    public MoveResponseDTO getMoveById(@PathVariable("move_id") UUID moveId) {
        return moveService.getMoveById(moveId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MoveResponseDTO createMove(@RequestBody @Validated MoveRequestDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return moveService.createMove(body);
    }

    @PutMapping("/{move_id}")
    public MoveResponseDTO updateMove(@PathVariable("move_id") UUID moveId, @RequestBody @Validated MoveRequestDTO body) {
        return moveService.updateMove(moveId, body);
    }

    @DeleteMapping("/{move_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMove(@PathVariable("move_id") UUID moveId) {
        moveService.deleteMove(moveId);
    }

    @GetMapping("/game/{game_id}")
    public List<MoveResponseDTO> getMovesByGame(@PathVariable("game_id") UUID gameId) {
        return moveService.getMovesByGame(gameId);
    }

    @GetMapping("/player/{player_id}")
    public List<MoveResponseDTO> getMovesByPlayer(@PathVariable("player_id") UUID playerId) {
        return moveService.getMovesByPlayer(playerId);
    }
}

