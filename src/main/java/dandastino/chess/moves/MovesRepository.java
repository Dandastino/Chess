package dandastino.chess.moves;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovesRepository extends JpaRepository<Move, UUID> {
    @Query("SELECT m FROM Move m WHERE m.gameAnalysis.gameId = :gameId")
    List<Move> findByGameId(@Param("gameId") UUID gameId);

    @Query("SELECT m FROM Move m WHERE m.userMove.user_id = :playerId")
    List<Move> findByPlayerId(@Param("playerId") UUID playerId);
}
