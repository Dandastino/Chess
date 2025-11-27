package dandastino.chess.users;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsersService {

    @Autowired
    private UsersRepository users_repo;

    public List<User> getUsers() {
        return this.users_repo.findAll();
    }

    public NewUsersRespDTO createUser(NewUsersDTO body){
        // TODO a lot of different controls

        if(body.username().length() < 3) throw new ValidationException("Username must be at least 3 characters long");

        User newUser = new User(body.bio(), body.avatar_url(), body.created_at(), body.elo_rating(), body.password(), body.email(), body.username());
        User saved = this.users_repo.save(newUser);
        System.out.println(saved.getUsername() + " was created");

        return new NewUsersRespDTO(saved.getId());
    }

    public User getUserById(UUID userId){
        return this.users_repo.findById(userId).orElseThrow(() -> new NotFoundException(userId));
    }

    public User updateUser(UUID userId, NewUsersDTO body){
        User found = getUserById(userId);

        // TODO a lot of different controls

        found.setBio(body.bio());
        found.setAvatar_url(body.avatar_url());
        found.setElo_rating(body.elo_rating());
        found.setPassword(body.password());
        found.setEmail(body.email());
        found.setUsername(body.username());

        return this.users_repo.save(found);
    }

    public void deleteUser(UUID userId){
        User found = getUserById(userId);
        this.users_repo.delete(found);
    }
}
