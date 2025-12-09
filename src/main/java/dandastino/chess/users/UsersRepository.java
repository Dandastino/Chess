package dandastino.chess.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {

    @Query("SELECT COUNT(f) FROM Friend f WHERE f.friend1.user_id = :userID")
    Long countFriends(UUID userID);

    // ************************** QUERY DERIVE ****************************************

    @Override
    Optional<User> findById(UUID uuid);

    //
    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<User> getAll();

    Optional<User> findByUsername(String username);
}
