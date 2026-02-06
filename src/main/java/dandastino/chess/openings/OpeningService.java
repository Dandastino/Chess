package dandastino.chess.openings;

import dandastino.chess.exceptions.AlreadyExists;
import dandastino.chess.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OpeningService {

    @Autowired
    private OpeningsRepository openingsRepository;

    public List<OpeningResponseDTO> getAllOpenings() {
        return openingsRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public OpeningResponseDTO getOpeningById(UUID openingId) {
        Opening opening = openingsRepository.findById(openingId)
                .orElseThrow(() -> new NotFoundException(openingId));
        return convertToDTO(opening);
    }

    public OpeningResponseDTO createOpening(OpeningDTO openingDTO) {
        if (openingsRepository.existsByEcoCode(openingDTO.ecoCode())) {
            throw new AlreadyExists("Opening with ECO code " + openingDTO.ecoCode() + " already exists");
        }

        Opening opening = new Opening();
        opening.setName(openingDTO.name());
        opening.setEco_code(openingDTO.ecoCode());
        opening.setFen_start(openingDTO.fenStart());
        opening.setMoves(openingDTO.moves());

        Opening saved = openingsRepository.save(opening);
        return convertToDTO(saved);
    }

    public OpeningResponseDTO updateOpening(UUID openingId, OpeningDTO openingDTO) {
        Opening opening = openingsRepository.findById(openingId)
                .orElseThrow(() -> new NotFoundException(openingId));

        if (openingDTO.name() != null) {
            opening.setName(openingDTO.name());
        }
        if (openingDTO.ecoCode() != null) {
            opening.setEco_code(openingDTO.ecoCode());
        }
        if (openingDTO.fenStart() != null) {
            opening.setFen_start(openingDTO.fenStart());
        }
        if (openingDTO.moves() != null) {
            opening.setMoves(openingDTO.moves());
        }

        Opening saved = openingsRepository.save(opening);
        return convertToDTO(saved);
    }

    public void deleteOpening(UUID openingId) {
        Opening opening = openingsRepository.findById(openingId)
                .orElseThrow(() -> new NotFoundException(openingId));
        openingsRepository.delete(opening);
    }

    public OpeningResponseDTO getOpeningByEcoCode(String ecoCode) {
        Opening opening = openingsRepository.findByEcoCode(ecoCode)
                .orElseThrow(() -> new NotFoundException("Opening with ECO code " + ecoCode + " not found"));
        return convertToDTO(opening);
    }

    private OpeningResponseDTO convertToDTO(Opening opening) {
        return new OpeningResponseDTO(
                opening.getOpening_id(),
                opening.getName(),
                opening.getEco_code(),
                opening.getFen_start(),
                opening.getMoves()
        );
    }
}

