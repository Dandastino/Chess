package dandastino.chess.cheatingAnalyses;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.games.Game;
import dandastino.chess.games.GamesRepository;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CheatingAnalysisService {

    @Autowired
    private CheatingAnalysesRepository cheatingAnalysesRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<CheatingAnalysisResponseDTO> getAllCheatingAnalyses() {
        return cheatingAnalysesRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public CheatingAnalysisResponseDTO getCheatingAnalysisById(UUID cheatingAnalysisId) {
        CheatingAnalysis cheatingAnalysis = cheatingAnalysesRepository.findById(cheatingAnalysisId)
                .orElseThrow(() -> new NotFoundException(cheatingAnalysisId));
        return convertToDTO(cheatingAnalysis);
    }

    public CheatingAnalysisResponseDTO createCheatingAnalysis(CheatingAnalysisDTO cheatingAnalysisDTO) {
        Game game = gamesRepository.findById(cheatingAnalysisDTO.gameId())
                .orElseThrow(() -> new NotFoundException("Game not found"));
        User user = usersRepository.findById(cheatingAnalysisDTO.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        CheatingAnalysis cheatingAnalysis = new CheatingAnalysis();
        cheatingAnalysis.setCheating_analysis_id(UUID.randomUUID()); // Generate UUID
        cheatingAnalysis.setCheating_game(game);
        cheatingAnalysis.setCheating_user(user);
        cheatingAnalysis.setMatch_accuracy_perc(cheatingAnalysisDTO.matchAccuracyPerc());
        cheatingAnalysis.setSuspicion_score(cheatingAnalysisDTO.suspicionScore());
        cheatingAnalysis.setCreated_at(LocalDateTime.now());

        CheatingAnalysis saved = cheatingAnalysesRepository.save(cheatingAnalysis);
        return convertToDTO(saved);
    }

    public CheatingAnalysisResponseDTO updateCheatingAnalysis(UUID cheatingAnalysisId, CheatingAnalysisDTO cheatingAnalysisDTO) {
        CheatingAnalysis cheatingAnalysis = cheatingAnalysesRepository.findById(cheatingAnalysisId)
                .orElseThrow(() -> new NotFoundException(cheatingAnalysisId));

        cheatingAnalysis.setMatch_accuracy_perc(cheatingAnalysisDTO.matchAccuracyPerc());
        cheatingAnalysis.setSuspicion_score(cheatingAnalysisDTO.suspicionScore());

        CheatingAnalysis saved = cheatingAnalysesRepository.save(cheatingAnalysis);
        return convertToDTO(saved);
    }

    public void deleteCheatingAnalysis(UUID cheatingAnalysisId) {
        CheatingAnalysis cheatingAnalysis = cheatingAnalysesRepository.findById(cheatingAnalysisId)
                .orElseThrow(() -> new NotFoundException(cheatingAnalysisId));
        cheatingAnalysesRepository.delete(cheatingAnalysis);
    }

    public List<CheatingAnalysisResponseDTO> getCheatingAnalysesByGame(UUID gameId) {
        return cheatingAnalysesRepository.findByGameId(gameId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<CheatingAnalysisResponseDTO> getCheatingAnalysesByUser(UUID userId) {
        return cheatingAnalysesRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private CheatingAnalysisResponseDTO convertToDTO(CheatingAnalysis cheatingAnalysis) {
        return new CheatingAnalysisResponseDTO(
                cheatingAnalysis.getCheating_analysis_id(),
                cheatingAnalysis.getCheating_game() != null ? cheatingAnalysis.getCheating_game().getGame_id() : null,
                cheatingAnalysis.getCheating_user() != null ? cheatingAnalysis.getCheating_user().getUser_id() : null,
                cheatingAnalysis.getMatch_accuracy_perc(),
                cheatingAnalysis.getSuspicion_score(),
                cheatingAnalysis.getCreated_at()
        );
    }
}

