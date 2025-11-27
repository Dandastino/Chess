package dandastino.chess.messages;

import dandastino.chess.games.Game;
import dandastino.chess.users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="messages")
public class Message {

    @Id
    @GeneratedValue
    private UUID message_id;
    private String content;
    private LocalDateTime created_at;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public Message(){}

    public Message(UUID message_id, String content, LocalDateTime created_at, User sender, Game game) {
        this.message_id = message_id;
        this.content = content;
        this.created_at = created_at;
        this.sender = sender;
        this.game = game;
    }

    public UUID getMessage_id() {
        return message_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message_id=" + message_id +
                ", content='" + content + '\'' +
                ", created_at=" + created_at +
                ", sender=" + sender +
                ", game=" + game +
                '}';
    }
}
