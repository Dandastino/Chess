package dandastino.chess.users;
import dandastino.chess.friends.Friend;
import dandastino.chess.user_settings.UserSetting;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name="users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID user_id;
    @Column(unique = true, nullable = false, name = "username")
    private String username;
    @Column(unique = true, nullable = false, name = "email")
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private int elo_rating;
    @Column(nullable = false)
    private LocalDateTime created_at;
    private String avatar_url;
    private String bio;
    @Enumerated(EnumType.STRING)
    private Country country;

    @OneToMany(mappedBy = "friend1")
    private List<Friend> friend1;

    @OneToMany(mappedBy = "friend2")
    private List<Friend> friend2;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSetting userSetting;

    public User() {

    }

    public User(String bio, String avatar_url, LocalDateTime created_at, int elo_rating, String password, String email, String username) {
        this.bio = bio;
        this.avatar_url = avatar_url;
        this.created_at = created_at;
        this.elo_rating = elo_rating;
        this.password = password;
        this.email = email;
        this.username = username;
    }

    public UUID getId() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getElo_rating() {
        return elo_rating;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getBio() {
        return bio;
    }

    public Country getCountry() {
        return country;
    }

    public void setId(UUID user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setElo_rating(int elo_rating) {
        this.elo_rating = elo_rating;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + user_id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", elo_rating=" + elo_rating +
                ", created_at='" + created_at + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", bio='" + bio + '\'' +
                ", country=" + country +
                '}';
    }
}
