package dandastino.chess.messages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessagesRepository extends JpaRepository<Message, UUID> {
    @Query("SELECT m FROM Message m WHERE m.game.gameId = :gameId")
    List<Message> findByGameId(@Param("gameId") UUID gameId);

    @Query("SELECT m FROM Message m WHERE m.sender.user_id = :userId")
    List<Message> findByUserId(@Param("userId") UUID userId);
}
