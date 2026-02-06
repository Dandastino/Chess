package dandastino.chess.users;

import dandastino.chess.exceptions.ValidationException;
import dandastino.chess.utility.Country;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public NewUsersRespDTO createUser(@RequestBody @Valid NewUsersDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return this.usersService.createUser(body);
    }

    @GetMapping("/{user_id}")
    public User getUserById(@PathVariable("user_id") UUID userId){
        return this.usersService.getUserById(userId);
    }

    @GetMapping("/user_username")
    public User getUserByUsername(@RequestParam("username") String username){ return this.usersService.getUserByUsername(username);}

    @PutMapping("/{user_id}")
    @PreAuthorize("#userId == authentication.principal.id")
    public User updateUser(@PathVariable("user_id") UUID userId, @RequestBody NewUsersDTO body) {
        return this.usersService.updateUser(userId, body);
    }

    // PATCH with query parameters for partial updates - NO form-data, NO body
    @PatchMapping("/{user_id}")
    @PreAuthorize("#userId == authentication.principal.id")
    public User partialUpdateUser(
            @PathVariable("user_id") UUID userId,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Country country,
            @RequestParam(required = false) Integer elo_rating
    ) {
        return this.usersService.partialUpdateUser(userId, bio, username, email, country, elo_rating);
    }

    @DeleteMapping("/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("#userId == authentication.principal.id")
    public void deleteUser(@PathVariable("user_id") UUID userId) {
        this.usersService.deleteUser(userId);
    }

    @PatchMapping("/{user_id}/avatar")
    @PreAuthorize("#userId == authentication.principal.id")
    @ResponseStatus(HttpStatus.OK)
    public void updateAvatar(@PathVariable("user_id") UUID userId, @RequestParam("avatar") MultipartFile file) {
        this.usersService.updateUserUrl(userId, file);
    }

    @GetMapping("/leaderboard")
    @ResponseStatus(HttpStatus.OK)
    public List<LeaderboardResponseDTO> getLeaderboard(@RequestParam(value = "limit", defaultValue = "100") int limit) {
        return this.usersService.getLeaderboard(limit);
    }

    @GetMapping("/{user_id}/stats")
    @ResponseStatus(HttpStatus.OK)
    public Object getUserStats(@PathVariable("user_id") UUID userId) {
        return this.usersService.getUserStats(userId);
    }

    @PatchMapping("/{user_id}/password")
    @PreAuthorize("#userId == authentication.principal.id")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@PathVariable("user_id") UUID userId, @RequestBody ChangePasswordDTO passwordChange) {
        this.usersService.changePassword(userId, passwordChange);
    }
}