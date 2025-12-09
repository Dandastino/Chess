package dandastino.chess.moves;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovesRepository extends JpaRepository<Move, UUID> {

    List<Move> findByGameId(UUID gameId);

}
