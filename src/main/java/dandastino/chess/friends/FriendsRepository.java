package dandastino.chess.friends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendsRepository extends JpaRepository<Friend, UUID> {
    @Query("SELECT f FROM Friend f WHERE f.friend1.user_id = :userId OR f.friend2.user_id = :userId")
    List<Friend> findByUser1IdOrUser2Id(@Param("userId") UUID userId);

    @Query("SELECT f FROM Friend f WHERE (f.friend1.user_id = :user1Id AND f.friend2.user_id = :user2Id) OR (f.friend1.user_id = :user2Id AND f.friend2.user_id = :user1Id)")
    Optional<Friend> findByUser1IdAndUser2IdOrUser2IdAndUser1Id(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);
}
