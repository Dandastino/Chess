package dandastino.chess.gamesOpenings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameOpeningsRepository extends JpaRepository <GameOpening, UUID> {
}
