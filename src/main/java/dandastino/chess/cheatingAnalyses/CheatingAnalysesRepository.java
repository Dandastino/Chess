package dandastino.chess.cheatingAnalyses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CheatingAnalysesRepository extends JpaRepository<CheatingAnalysis, UUID> {
    @Query("SELECT ca FROM CheatingAnalysis ca WHERE ca.cheating_game.gameId = :gameId")
    List<CheatingAnalysis> findByGameId(@Param("gameId") UUID gameId);

    @Query("SELECT ca FROM CheatingAnalysis ca WHERE ca.cheating_user.user_id = :userId")
    List<CheatingAnalysis> findByUserId(@Param("userId") UUID userId);
}
