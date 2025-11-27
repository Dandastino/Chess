package dandastino.chess.friends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FriendsRepository extends JpaRepository<Friend, UUID> {
}
