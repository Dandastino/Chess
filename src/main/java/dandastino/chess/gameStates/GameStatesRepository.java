package dandastino.chess.gameStates;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameStatesRepository extends JpaRepository<GameState, UUID> {
    @Query("SELECT gs FROM GameState gs WHERE gs.game.gameId = :gameId")
    List<GameState> findByGameId(@Param("gameId") UUID gameId);
}
