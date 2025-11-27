package dandastino.chess.users;

import java.time.LocalDateTime;

public record NewUsersDTO(String bio, String avatar_url, LocalDateTime created_at, int elo_rating, String password, String email, String username, Country country){
}
