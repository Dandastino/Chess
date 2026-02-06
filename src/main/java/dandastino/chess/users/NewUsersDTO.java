package dandastino.chess.users;

import dandastino.chess.utility.Country;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;

public record NewUsersDTO(@Size(message = "At most you can have 255 char")
                          @Size(max = 255, message = "The bio can be more than 255 char")
                          String bio,
                          String avatar_url,

                          LocalDateTime created_at,
                          @DefaultValue()
                          int elo_rating,
                          @NotBlank(message = "You must provide a password")
                          @NotNull(message = "Password cannot be null")
                          @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#?!@$%^&*-]).{8,255}$",
                                  message = "The password has to have at least one capital character, one small character and one special character.")
                          String password,
                          @NotBlank(message = "You must provide and email address")
                          @Email(message = "Insert an email in the correct format")
                          String email,
                          @NotBlank(message = "You must provide a username")
                          @Size(min = 4, max = 30, message = "The username has to be between 4 a 30 char")
                          String username,
                          @NotNull(message = "You must provide a country")
                          Country country){
}
