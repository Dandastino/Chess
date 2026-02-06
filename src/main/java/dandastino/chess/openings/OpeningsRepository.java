package dandastino.chess.openings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpeningsRepository extends JpaRepository<Opening, UUID> {

    @Query("SELECT COUNT(o) > 0 FROM Opening o WHERE o.eco_code = :ecoCode")
    boolean existsByEcoCode(@Param("ecoCode") String ecoCode);

    @Query("SELECT o FROM Opening o WHERE o.eco_code = :ecoCode")
    Optional<Opening> findByEcoCode(@Param("ecoCode") String ecoCode);
}
