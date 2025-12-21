package dandastino.chess.moveAnalyses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MoveAnalysesRepository extends JpaRepository<MoveAnalysis, UUID> {
    @Query("SELECT ma FROM MoveAnalysis ma WHERE ma.move.moveId = :moveId")
    Optional<MoveAnalysis> findByMoveId(@Param("moveId") UUID moveId);
}
