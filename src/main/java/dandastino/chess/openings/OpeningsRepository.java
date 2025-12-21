package dandastino.chess.openings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpeningsRepository extends JpaRepository<Opening, UUID> {
    boolean existsByEcoCode(String ecoCode);
    Optional<Opening> findByEcoCode(String ecoCode);
}
