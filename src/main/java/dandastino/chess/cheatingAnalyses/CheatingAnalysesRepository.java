package dandastino.chess.cheatingAnalyses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CheatingAnalysesRepository extends JpaRepository<CheatingAnalysis, UUID> {
}
