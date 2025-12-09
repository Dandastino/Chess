package dandastino.chess.Auth;

import dandastino.chess.users.User;
import dandastino.chess.users.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthTools authTools;
    @Autowired
    private UsersService usersService;

    // Check credentials and generate JWT
    public String login(AuthDTO body){
        User found = this.usersService.findByUsername(body.username());

        if (body.password().equals(found.getPassword())){
            return authTools.createToken(found);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
