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

    public MessageResponseDTO updateMessage(UUID messageId, MessageDTO messageDTO) {
        Message message = messagesRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(messageId));

        if (messageDTO.content() != null) {
            message.setContent(messageDTO.content());
        }

        Message saved = messagesRepository.save(message);
        return convertToDTO(saved);
    }

    public void deleteMessage(UUID messageId) {
        Message message = messagesRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException(messageId));
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

    private MessageResponseDTO convertToDTO(Message message) {
        return new MessageResponseDTO(
                message.getMessage_id(),
                message.getGame() != null ? message.getGame().getGame_id() : null,
                message.getSender() != null ? message.getSender().getUser_id() : null,
                message.getContent(),
                message.getTimestamp()
        );
    }
}

