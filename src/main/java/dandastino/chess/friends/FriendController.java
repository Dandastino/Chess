package dandastino.chess.friends;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    public List<FriendResponseDTO> getAllFriendships() {
        return friendService.getAllFriendships();
    }

    @GetMapping("/{friendship_id}")
    public FriendResponseDTO getFriendshipById(@PathVariable("friendship_id") UUID friendshipId) {
        return friendService.getFriendshipById(friendshipId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FriendResponseDTO createFriendship(@RequestBody @Validated FriendDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return friendService.createFriendship(body);
    }

    @DeleteMapping("/{friendship_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriendship(@PathVariable("friendship_id") UUID friendshipId) {
        friendService.deleteFriendship(friendshipId);
    }

    @GetMapping("/user/{user_id}")
    public List<FriendResponseDTO> getFriendshipsByUser(@PathVariable("user_id") UUID userId) {
        return friendService.getFriendshipsByUser(userId);
    }

    @DeleteMapping("/user/{user1_id}/user/{user2_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriendship(@PathVariable("user1_id") UUID user1Id, @PathVariable("user2_id") UUID user2Id) {
        friendService.removeFriendship(user1Id, user2Id);
    }
}

