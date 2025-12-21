
package dandastino.chess.friends;

import dandastino.chess.users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name="friendships")
@Entity
public class Friend {

    @Id
    @GeneratedValue
    @Column(name = "friendship_id")
    private UUID friendship_id;
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User friend1;

    @ManyToOne
    @JoinColumn(name = "user2_id")
    private User friend2;

    public Friend(){}

    public Friend(User friend1, User friend2, LocalDateTime created_at) {
        this.friend1 = friend1;
        this.friend2 = friend2;
        this.created_at = created_at;
    }

    public UUID getFriendship_id() {
        return friendship_id;
    }

    public User getFriend1() {
        return friend1;
    }

    public void setFriend1(User friend1) {
        this.friend1 = friend1;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public User getFriend2() {
        return friend2;
    }

    public void setFriend2(User friend2) {
        this.friend2 = friend2;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "friendship_id=" + friendship_id +
                ", friend1=" + friend1 +
                ", friend2=" + friend2 +
                '}';
    }
}