package dandastino.chess.moveAnalyses;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/move-analyses")
public class MoveAnalysisController {

    private final MoveAnalysisService moveAnalysisService;

    public MoveAnalysisController(MoveAnalysisService moveAnalysisService) {
        this.moveAnalysisService = moveAnalysisService;
    }

    @GetMapping
    public List<MoveAnalysisResponseDTO> getAllMoveAnalyses() {
        return moveAnalysisService.getAllMoveAnalyses();
    }

    @GetMapping("/{move_analysis_id}")
    public MoveAnalysisResponseDTO getMoveAnalysisById(@PathVariable("move_analysis_id") UUID moveAnalysisId) {
        return moveAnalysisService.getMoveAnalysisById(moveAnalysisId);
    }

    @GetMapping("/move/{move_id}")
    public MoveAnalysisResponseDTO getMoveAnalysisByMoveId(@PathVariable("move_id") UUID moveId) {
        return moveAnalysisService.getMoveAnalysisByMoveId(moveId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MoveAnalysisResponseDTO createMoveAnalysis(@RequestBody @Validated MoveAnalysisDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return moveAnalysisService.createMoveAnalysis(body);
    }

    @PutMapping("/{move_analysis_id}")
    public MoveAnalysisResponseDTO updateMoveAnalysis(@PathVariable("move_analysis_id") UUID moveAnalysisId, @RequestBody @Validated MoveAnalysisDTO body) {
        return moveAnalysisService.updateMoveAnalysis(moveAnalysisId, body);
    }

    @DeleteMapping("/{move_analysis_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMoveAnalysis(@PathVariable("move_analysis_id") UUID moveAnalysisId) {
        moveAnalysisService.deleteMoveAnalysis(moveAnalysisId);
    }
}

