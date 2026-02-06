package dandastino.chess.users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import dandastino.chess.cheatingAnalyses.CheatingAnalysis;
import dandastino.chess.friends.Friend;
import dandastino.chess.games.Game;
import dandastino.chess.messages.Message;
import dandastino.chess.moves.Move;
import dandastino.chess.userSettings.UserSetting;
import dandastino.chess.utility.Country;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name="users")
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID user_id;
    
    @Column(name = "avatar_url")
    private String avatar_url;
    
    @Column(name = "bio")
    private String bio;
    
    @Column(name = "country")
    @Enumerated(EnumType.STRING)
    private Country country;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;
    
    @Column(name = "elo_rating", nullable = false)
    private int elo_rating = 1200;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType type;
    
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @OneToMany(mappedBy = "friend1")
    private List<Friend> friend1;

    @JsonIgnore
    @OneToMany(mappedBy = "friend2")
    private List<Friend> friend2;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSetting userSetting;

    @JsonIgnore
    @OneToMany(mappedBy = "whitePlayer")
    private List<Game> white;

    @JsonIgnore
    @OneToMany(mappedBy = "blackPlayer")
    private List<Game> black;

    @JsonIgnore
    @OneToMany(mappedBy = "winner")
    private List<Game> winner;

    @JsonIgnore
    @OneToMany(mappedBy = "cheating_user")
    private List<CheatingAnalysis> cheating_user;

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private List<Message> messages;

    @JsonIgnore
    @OneToMany(mappedBy = "userMove")
    private List<Move> userMove;

    public User() {

    }

    public User(String bio, String avatar_url, LocalDateTime created_at, int elo_rating, String passwordHash, String email, String username, Country country) {
        this.avatar_url = avatar_url;
        this.bio = bio;
        this.country = country;
        this.created_at = created_at;
        this.elo_rating = elo_rating;
        this.email = email;
        this.passwordHash = passwordHash;
        this.type = UserType.HUMAN;
        this.username = username;
        // Initialize UserSetting immediately
        this.userSetting = new UserSetting(LocalDateTime.now(), true, false, false, "English", "Blitz", "Light");
        this.userSetting.setUser(this);
    }

    @JsonIgnore
    public UUID getId() {
        return user_id;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.type.name()));
    }

    @Override
    public String getUsername(){
        return this.username;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return passwordHash;
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

    public void setPassword(String passwordHash) {
        this.passwordHash = passwordHash;
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
                ", passwordHash='[PROTECTED]'" +
                ", elo_rating=" + elo_rating +
                ", created_at='" + created_at + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", bio='" + bio + '\'' +
                ", country=" + country +
                '}';
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

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}