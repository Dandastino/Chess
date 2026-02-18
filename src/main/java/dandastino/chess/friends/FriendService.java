package dandastino.chess.friends;

import dandastino.chess.exceptions.AlreadyExists;
import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FriendService {

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<FriendResponseDTO> getAllFriendships() {
        return friendsRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public FriendResponseDTO getFriendshipById(UUID friendshipId) {
        Friend friend = friendsRepository.findById(friendshipId)
                .orElseThrow(() -> new NotFoundException(friendshipId));
        return convertToDTO(friend);
    }

    public FriendResponseDTO createFriendship(FriendDTO friendDTO) {
        User user1 = usersRepository.findById(friendDTO.user1Id())
                .orElseThrow(() -> new NotFoundException("User 1 not found"));
        User user2 = usersRepository.findById(friendDTO.user2Id())
                .orElseThrow(() -> new NotFoundException("User 2 not found"));

        // Bots cannot have friends
        if (user1.getType().equals(dandastino.chess.users.UserType.BOT) || user2.getType().equals(dandastino.chess.users.UserType.BOT)) {
            throw new dandastino.chess.exceptions.ValidationException("Bots cannot have friends");
        }

        // Check if friendship already exists
        if (friendshipExists(friendDTO.user1Id(), friendDTO.user2Id())) {
            throw new AlreadyExists("Friendship already exists between these users");
        }

        Friend friend = new Friend();
        friend.setFriend1(user1);
        friend.setFriend2(user2);
        friend.setCreated_at(LocalDateTime.now());

        Friend saved = friendsRepository.save(friend);
        return convertToDTO(saved);
    }

    public void deleteFriendship(UUID friendshipId) {
        Friend friend = friendsRepository.findById(friendshipId)
                .orElseThrow(() -> new NotFoundException(friendshipId));
        friendsRepository.delete(friend);
    }

    public List<FriendResponseDTO> getFriendshipsByUser(UUID userId) {
        return friendsRepository.findByUser1IdOrUser2Id(userId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public void removeFriendship(UUID user1Id, UUID user2Id) {
        Friend friendship = friendsRepository.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(user1Id, user2Id)
                .orElseThrow(() -> new NotFoundException("Friendship not found"));
        friendsRepository.delete(friendship);
    }

    private boolean friendshipExists(UUID user1Id, UUID user2Id) {
        return friendsRepository.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(user1Id, user2Id).isPresent();
    }

    private FriendResponseDTO convertToDTO(Friend friend) {
        return new FriendResponseDTO(
                friend.getFriendship_id(),
                friend.getFriend1().getUser_id(),
                friend.getFriend2().getUser_id(),
                friend.getCreated_at()
        );
    }
}

