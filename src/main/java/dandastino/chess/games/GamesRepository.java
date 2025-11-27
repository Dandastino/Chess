package dandastino.chess.games;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GamesRepository extends JpaRepository<Game, UUID> {
}
