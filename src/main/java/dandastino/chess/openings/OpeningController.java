package dandastino.chess.openings;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/openings")
public class OpeningController {

    private final OpeningService openingService;

    public OpeningController(OpeningService openingService) {
        this.openingService = openingService;
    }

    @GetMapping
    public List<OpeningResponseDTO> getAllOpenings() {
        return openingService.getAllOpenings();
    }

    @GetMapping("/{opening_id}")
    public OpeningResponseDTO getOpeningById(@PathVariable("opening_id") UUID openingId) {
        return openingService.getOpeningById(openingId);
    }

    @GetMapping("/eco/{eco_code}")
    public OpeningResponseDTO getOpeningByEcoCode(@PathVariable("eco_code") String ecoCode) {
        return openingService.getOpeningByEcoCode(ecoCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OpeningResponseDTO createOpening(@RequestBody @Validated OpeningDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return openingService.createOpening(body);
    }

    @PutMapping("/{opening_id}")
    public OpeningResponseDTO updateOpening(@PathVariable("opening_id") UUID openingId, @RequestBody @Validated OpeningDTO body) {
        return openingService.updateOpening(openingId, body);
    }

    @DeleteMapping("/{opening_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOpening(@PathVariable("opening_id") UUID openingId) {
        openingService.deleteOpening(openingId);
    }
}

