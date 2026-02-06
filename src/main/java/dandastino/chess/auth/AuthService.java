package dandastino.chess.auth;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.exceptions.UnauthorizeException;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthTools authTools;
    @Autowired
    private UsersService usersService;
    @Autowired
    private PasswordEncoder bcrypt;

    public String login(AuthDTO body) {
        if (body == null || body.username() == null || body.username().isBlank()
                || body.password() == null || body.password().isBlank()) {
            throw new UnauthorizeException("Username and password are required");
        }
        User found;
        try {
            found = usersService.findByUsername(body.username().trim());
        } catch (NotFoundException e) {
            throw new UnauthorizeException("Invalid credentials");
        }
        String storedPassword = found.getPassword();
        if (storedPassword == null || !bcrypt.matches(body.password(), storedPassword)) {
            throw new UnauthorizeException("Invalid credentials");
        }
        return authTools.createToken(found);
    }

    public String refreshToken(String token) {
        try {
            String accessToken = token.replace("Bearer ", "");
            authTools.validateToken(accessToken);
            java.util.UUID userId = authTools.getUserIdFromToken(accessToken);
            User user = usersService.getUserById(userId);
            return authTools.createToken(user);
        } catch (Exception e) {
            throw new UnauthorizeException("Invalid or expired token");
        }
    }
}
