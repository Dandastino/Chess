package dandastino.chess.cheatingAnalyses;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cheating-analyses")
public class CheatingAnalysisController {

    private final CheatingAnalysisService cheatingAnalysisService;

    public CheatingAnalysisController(CheatingAnalysisService cheatingAnalysisService) {
        this.cheatingAnalysisService = cheatingAnalysisService;
    }

    @GetMapping
    public List<CheatingAnalysisResponseDTO> getAllCheatingAnalyses() {
        return cheatingAnalysisService.getAllCheatingAnalyses();
    }

    @GetMapping("/{cheating_analysis_id}")
    public CheatingAnalysisResponseDTO getCheatingAnalysisById(@PathVariable("cheating_analysis_id") UUID cheatingAnalysisId) {
        return cheatingAnalysisService.getCheatingAnalysisById(cheatingAnalysisId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheatingAnalysisResponseDTO createCheatingAnalysis(@RequestBody @Validated CheatingAnalysisDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return cheatingAnalysisService.createCheatingAnalysis(body);
    }

    @PutMapping("/{cheating_analysis_id}")
    public CheatingAnalysisResponseDTO updateCheatingAnalysis(@PathVariable("cheating_analysis_id") UUID cheatingAnalysisId, @RequestBody @Validated CheatingAnalysisDTO body) {
        return cheatingAnalysisService.updateCheatingAnalysis(cheatingAnalysisId, body);
    }

    @DeleteMapping("/{cheating_analysis_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCheatingAnalysis(@PathVariable("cheating_analysis_id") UUID cheatingAnalysisId) {
        cheatingAnalysisService.deleteCheatingAnalysis(cheatingAnalysisId);
    }

    @GetMapping("/game/{game_id}")
    public List<CheatingAnalysisResponseDTO> getCheatingAnalysesByGame(@PathVariable("game_id") UUID gameId) {
        return cheatingAnalysisService.getCheatingAnalysesByGame(gameId);
    }

    @GetMapping("/user/{user_id}")
    public List<CheatingAnalysisResponseDTO> getCheatingAnalysesByUser(@PathVariable("user_id") UUID userId) {
        return cheatingAnalysisService.getCheatingAnalysesByUser(userId);
    }
}

