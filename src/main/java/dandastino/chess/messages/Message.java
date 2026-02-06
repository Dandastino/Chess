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
    @Column(name = "message_id")
    private UUID message_id;
    @Column(name = "content")
    private String content;
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean read = false;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public Message(){}

    public Message(UUID message_id, String content, LocalDateTime timestamp, User sender, Game game) {
        this.message_id = message_id;
        this.content = content;
        this.timestamp = timestamp;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message_id=" + message_id +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", sender=" + sender +
                ", game=" + game +
                '}';
    }


}
