package dandastino.chess.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record NewUsersDTO(
                          String bio,
                          String avatar_url,
                          LocalDateTime created_at,
                          int elo_rating,
                          @Pattern(regexp = "/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/\n")
                          String password,
                          @Email(message = "Insert a real email")
                          String email,
                          @NotBlank(message = "the username is mandatory")
                          @Size(message = "The username has to be between 4 a 30 char")
                          String username,
                          Country country){
}
