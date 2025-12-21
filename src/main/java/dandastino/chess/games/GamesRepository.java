package dandastino.chess.games;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GamesRepository extends JpaRepository<Game, UUID> {
    @Query("SELECT g FROM Game g WHERE g.whitePlayer.user_id = :playerId OR g.blackPlayer.user_id = :playerId")
    List<Game> findByWhitePlayerIdOrBlackPlayerId(@Param("playerId") UUID playerId);
    List<Game> findByStatus(Status status);
    @Query("SELECT g FROM Game g WHERE g.winner.user_id = :winnerId")
    List<Game> findByWinnerId(@Param("winnerId") UUID winnerId);
}
