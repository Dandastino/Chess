package dandastino.chess.userSettings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersSettingsRepository extends JpaRepository<UserSetting, UUID> {
    @Query("SELECT us FROM UserSetting us WHERE us.user.user_id = :userId")
    Optional<UserSetting> findByUserId(@Param("userId") UUID userId);
}
