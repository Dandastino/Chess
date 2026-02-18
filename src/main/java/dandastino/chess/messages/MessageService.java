package dandastino.chess.messages;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.games.Game;
import dandastino.chess.games.GamesRepository;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<MessageResponseDTO> getAllMessages() {
        return messagesRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public MessageResponseDTO getMessageById(UUID messageId) {
        Message message = messagesRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(messageId));
        return convertToDTO(message);
    }

    public MessageResponseDTO createMessage(MessageDTO messageDTO) {
        Game game = gamesRepository.findById(messageDTO.gameId())
                .orElseThrow(() -> new NotFoundException("Game not found"));
        User user = usersRepository.findById(messageDTO.userId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Message message = new Message();
        message.setGame(game);
        message.setSender(user);
        message.setContent(messageDTO.content());
        message.setTimestamp(LocalDateTime.now());

        Message saved = messagesRepository.save(message);
        return convertToDTO(saved);
    }

    public MessageResponseDTO updateMessage(UUID messageId, MessageUpdateDTO messageDTO) {
        Message message = messagesRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(messageId));

        // Bots cannot modify messages
        if (message.getSender().getType().equals(dandastino.chess.users.UserType.BOT)) {
            throw new dandastino.chess.exceptions.ValidationException("Bots cannot modify messages");
        }

        message.setContent(messageDTO.content());

        Message saved = messagesRepository.save(message);
        return convertToDTO(saved);
    }

    public void deleteMessage(UUID messageId) {
        Message message = messagesRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(messageId));

        // Bots cannot delete messages
        if (message.getSender().getType().equals(dandastino.chess.users.UserType.BOT)) {
            throw new dandastino.chess.exceptions.ValidationException("Bots cannot delete messages");
        }

        messagesRepository.delete(message);
    }

    public List<MessageResponseDTO> getMessagesByGame(UUID gameId) {
        return messagesRepository.findByGameId(gameId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<MessageResponseDTO> getMessagesByUser(UUID userId) {
        return messagesRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<MessageSimpleDTO> getMessagesBetweenUsers(UUID userId1, UUID userId2) {
        usersRepository.findById(userId1)
                .orElseThrow(() -> new NotFoundException("User 1 not found"));
        usersRepository.findById(userId2)
                .orElseThrow(() -> new NotFoundException("User 2 not found"));
        
        return messagesRepository.findAll().stream()
                .filter(m -> (m.getSender().getUser_id().equals(userId1) || m.getSender().getUser_id().equals(userId2)))
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .map(this::convertToSimpleDTO)
                .toList();
    }

    public MessageResponseDTO markMessageAsRead(UUID messageId) {
        Message message = messagesRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
        message.setRead(true);
        Message saved = messagesRepository.save(message);
        return convertToDTO(saved);
    }

    private MessageResponseDTO convertToDTO(Message message) {
        return new MessageResponseDTO(
                message.getMessage_id(),
                message.getGame() != null ? message.getGame().getGame_id() : null,
                message.getSender() != null ? message.getSender().getUser_id() : null,
                message.getContent(),
                message.getTimestamp()
        );
    }

    private MessageSimpleDTO convertToSimpleDTO(Message message) {
        return new MessageSimpleDTO(
                message.getContent(),
                message.getTimestamp(),
                message.getGame() != null ? message.getGame().getGame_id() : null
        );
    }
}

