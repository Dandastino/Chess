package dandastino.chess.moveAnalyses;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.moves.Move;
import dandastino.chess.moves.MovesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MoveAnalysisService {

    @Autowired
    private MoveAnalysesRepository moveAnalysesRepository;

    @Autowired
    private MovesRepository movesRepository;

    public List<MoveAnalysisResponseDTO> getAllMoveAnalyses() {
        return moveAnalysesRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public MoveAnalysisResponseDTO getMoveAnalysisById(UUID moveAnalysisId) {
        MoveAnalysis moveAnalysis = moveAnalysesRepository.findById(moveAnalysisId)
                .orElseThrow(() -> new NotFoundException(moveAnalysisId));
        return convertToDTO(moveAnalysis);
    }

    public MoveAnalysisResponseDTO createMoveAnalysis(MoveAnalysisDTO moveAnalysisDTO) {
        Move move = movesRepository.findById(moveAnalysisDTO.moveId())
                .orElseThrow(() -> new NotFoundException("Move not found"));

        MoveAnalysis moveAnalysis = new MoveAnalysis();
        moveAnalysis.setMove(move);
        moveAnalysis.setEvaluation_cp(moveAnalysisDTO.evaluationCp());
        moveAnalysis.setBest_move(moveAnalysisDTO.bestMove());
        moveAnalysis.setDepth(moveAnalysisDTO.depth());
        moveAnalysis.setReview(moveAnalysisDTO.review());

        MoveAnalysis saved = moveAnalysesRepository.save(moveAnalysis);
        return convertToDTO(saved);
    }

    public MoveAnalysisResponseDTO updateMoveAnalysis(UUID moveAnalysisId, MoveAnalysisDTO moveAnalysisDTO) {
        MoveAnalysis moveAnalysis = moveAnalysesRepository.findById(moveAnalysisId)
                .orElseThrow(() -> new NotFoundException(moveAnalysisId));

        if (moveAnalysisDTO.evaluationCp() != null) {
            moveAnalysis.setEvaluation_cp(moveAnalysisDTO.evaluationCp());
        }
        if (moveAnalysisDTO.bestMove() != null) {
            moveAnalysis.setBest_move(moveAnalysisDTO.bestMove());
        }
        if (moveAnalysisDTO.depth() > 0) {
            moveAnalysis.setDepth(moveAnalysisDTO.depth());
        }
        if (moveAnalysisDTO.review() != null) {
            moveAnalysis.setReview(moveAnalysisDTO.review());
        }

        MoveAnalysis saved = moveAnalysesRepository.save(moveAnalysis);
        return convertToDTO(saved);
    }

    public void deleteMoveAnalysis(UUID moveAnalysisId) {
        MoveAnalysis moveAnalysis = moveAnalysesRepository.findById(moveAnalysisId)
                .orElseThrow(() -> new NotFoundException(moveAnalysisId));
        moveAnalysesRepository.delete(moveAnalysis);
    }

    public MoveAnalysisResponseDTO getMoveAnalysisByMoveId(UUID moveId) {
        MoveAnalysis moveAnalysis = moveAnalysesRepository.findByMoveId(moveId)
                .orElseThrow(() -> new NotFoundException("Move analysis not found for move " + moveId));
        return convertToDTO(moveAnalysis);
    }

    private MoveAnalysisResponseDTO convertToDTO(MoveAnalysis moveAnalysis) {
        return new MoveAnalysisResponseDTO(
                moveAnalysis.getMove_analysis_id(),
                moveAnalysis.getMove() != null ? moveAnalysis.getMove().getMoveId() : null,
                moveAnalysis.getEvaluation_cp(),
                moveAnalysis.getBest_move(),
                moveAnalysis.getDepth(),
                moveAnalysis.getReview()
        );
    }
}

