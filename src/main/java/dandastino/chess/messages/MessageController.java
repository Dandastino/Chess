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
    public MessageResponseDTO updateMessage(@PathVariable("message_id") UUID messageId, @RequestBody @Validated MessageUpdateDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return messageService.updateMessage(messageId, body);
    }

    @DeleteMapping("/{message_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable("message_id") UUID messageId) {
        messageService.deleteMessage(messageId);
    }

    @GetMapping("/game/{game_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponseDTO> getMessagesByGame(@PathVariable("game_id") UUID gameId) {
        return messageService.getMessagesByGame(gameId);
    }

    @GetMapping("/user/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponseDTO> getMessagesByUser(@PathVariable("user_id") UUID userId) {
        return messageService.getMessagesByUser(userId);
    }

    @GetMapping("/between/{user_id_1}/{user_id_2}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageSimpleDTO> getMessagesBetweenUsers(
            @PathVariable("user_id_1") UUID userId1,
            @PathVariable("user_id_2") UUID userId2) {
        return messageService.getMessagesBetweenUsers(userId1, userId2);
    }

    @PostMapping("/{message_id}/read")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO markMessageAsRead(@PathVariable("message_id") UUID messageId) {
        return messageService.markMessageAsRead(messageId);
    }
}

