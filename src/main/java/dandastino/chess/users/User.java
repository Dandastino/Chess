package dandastino.chess.users;
import dandastino.chess.cheatingAnalyses.CheatingAnalysis;
import dandastino.chess.friends.Friend;
import dandastino.chess.games.Game;
import dandastino.chess.messages.Message;
import dandastino.chess.moves.Move;
import dandastino.chess.userSettings.UserSetting;
import dandastino.chess.utility.Country;
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @OneToMany(mappedBy = "friend1")
    private List<Friend> friend1;

    @OneToMany(mappedBy = "friend2")
    private List<Friend> friend2;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSetting userSetting;

    @OneToMany(mappedBy = "whitePlayer")
    private List<Game> white;

    @OneToMany(mappedBy = "blackPlayer")
    private List<Game> black;

    @OneToMany(mappedBy = "winner")
    private List<Game> winner;

    @OneToMany(mappedBy = "cheating_user")
    private List<CheatingAnalysis> cheating_user;

    @OneToMany(mappedBy = "sender")
    private List<Message> messages;

    @OneToMany(mappedBy = "userMove")
    private List<Move> userMove;

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
        this.elo_rating = 800;
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

    public UUID getUser_id() {
        return user_id;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public List<Friend> getFriend1() {
        return friend1;
    }

    public void setFriend1(List<Friend> friend1) {
        this.friend1 = friend1;
    }

    public List<Friend> getFriend2() {
        return friend2;
    }

    public void setFriend2(List<Friend> friend2) {
        this.friend2 = friend2;
    }

    public UserSetting getUserSetting() {
        return userSetting;
    }

    public void setUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }

    public List<Game> getWhite() {
        return white;
    }

    public void setWhite(List<Game> white) {
        this.white = white;
    }

    public List<Game> getBlack() {
        return black;
    }

    public void setBlack(List<Game> black) {
        this.black = black;
    }

    public List<Game> getWinner() {
        return winner;
    }

    public void setWinner(List<Game> winner) {
        this.winner = winner;
    }

    public List<CheatingAnalysis> getCheating_user() {
        return cheating_user;
    }

    public void setCheating_user(List<CheatingAnalysis> cheating_user) {
        this.cheating_user = cheating_user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Move> getUserMove() {
        return userMove;
    }

    public void setUserMove(List<Move> userMove) {
        this.userMove = userMove;
    }
}