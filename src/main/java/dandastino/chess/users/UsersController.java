package dandastino.chess.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public List<User> getUsers() {
        return this.usersService.getUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewUsersRespDTO createUser(@RequestBody NewUsersDTO body) {
        return this.usersService.createUser(body);
    }

    @GetMapping("/{user_id}")
    public User getUserById(@PathVariable("user_id") UUID userId){
        return this.usersService.getUserById(userId);
    }

    @PutMapping("/{user_id}")
    public User updateUser(@PathVariable("user_id") UUID userId, @RequestBody NewUsersDTO body){
        return this.usersService.updateUser(userId, body);
    }

    @DeleteMapping("/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("user_id") UUID userId){
        this.usersService.deleteUser(userId);
    }
}