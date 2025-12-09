package dandastino.chess.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(@Param("username") String username);
    boolean existsByEmail(@Param("email") String email);

    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID uuid);

    @Query("SELECT COUNT(f) FROM Friend f WHERE f.friend1.user_id = :userID")
    Long countFriends(UUID userID);


}
