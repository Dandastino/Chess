package dandastino.chess.messages;

import dandastino.chess.exceptions.ValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageResponseDTO> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/{message_id}")
    public MessageResponseDTO getMessageById(@PathVariable("message_id") UUID messageId) {
        return messageService.getMessageById(messageId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createMessage(@RequestBody @Validated MessageDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return messageService.createMessage(body);
    }

    @PutMapping("/{message_id}")
    public MessageResponseDTO updateMessage(@PathVariable("message_id") UUID messageId, @RequestBody @Validated MessageDTO body) {
        return messageService.updateMessage(messageId, body);
    }

    @DeleteMapping("/{message_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable("message_id") UUID messageId) {
        messageService.deleteMessage(messageId);
    }

    @GetMapping("/game/{game_id}")
    public List<MessageResponseDTO> getMessagesByGame(@PathVariable("game_id") UUID gameId) {
        return messageService.getMessagesByGame(gameId);
    }

    @GetMapping("/user/{user_id}")
    public List<MessageResponseDTO> getMessagesByUser(@PathVariable("user_id") UUID userId) {
        return messageService.getMessagesByUser(userId);
    }
}

