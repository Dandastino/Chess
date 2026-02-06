package dandastino.chess.gamesOpenings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameOpeningsRepository extends JpaRepository<GameOpening, UUID> {
    @Query("SELECT go FROM GameOpening go WHERE go.game.gameId = :gameId")
    List<GameOpening> findByGameId(@Param("gameId") UUID gameId);

    @Query("SELECT go FROM GameOpening go WHERE go.opening.opening_id = :openingId")
    List<GameOpening> findByOpeningId(@Param("openingId") UUID openingId);
}
