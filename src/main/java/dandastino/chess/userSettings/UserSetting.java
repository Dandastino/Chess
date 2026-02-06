package dandastino.chess.userSettings;

import dandastino.chess.users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name="user_settings")
@Entity
public class UserSetting {

    @Id
    @GeneratedValue
    @Column(name = "user_setting_id")
    private UUID user_setting_id;
    @Column(name = "theme")
    private String theme;
    @Column(name = "preferred_time_control")
    private String preferred_time_control;
    @Column(name = "language")
    private String language;
    @Column(name = "allow_engine_analysis")
    private Boolean allow_engine_analysis;
    @Column(name = "show_move_suggestions")
    private Boolean show_move_suggestions;
    @Column(name = "notifications_enabled")
    private Boolean notifications_enabled;
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserSetting(){}

    public UserSetting(LocalDateTime created_at, boolean notifications_enabled, boolean show_move_suggestions, boolean allow_engine_analysis, String language, String preferred_time_control, String theme) {
        this.created_at = created_at;
        this.notifications_enabled = notifications_enabled;
        this.show_move_suggestions = show_move_suggestions;
        this.allow_engine_analysis = allow_engine_analysis;
        this.language = language;
        this.preferred_time_control = preferred_time_control;
        this.theme = theme;
    }

    public UUID getUser_setting_id() {
        return user_setting_id;
    }

    public void setUser_setting_id(UUID user_setting_id) {
        this.user_setting_id = user_setting_id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getPreferred_time_control() {
        return preferred_time_control;
    }

    public void setPreferred_time_control(String preferred_time_control) {
        this.preferred_time_control = preferred_time_control;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean isAllow_engine_analysis() {
        return allow_engine_analysis;
    }

    public void setAllow_engine_analysis(Boolean allow_engine_analysis) {
        this.allow_engine_analysis = allow_engine_analysis;
    }

    public Boolean isShow_move_suggestions() {
        return show_move_suggestions;
    }

    public void setShow_move_suggestions(Boolean show_move_suggestions) {
        this.show_move_suggestions = show_move_suggestions;
    }

    public Boolean isNotifications_enabled() {
        return notifications_enabled;
    }

    public void setNotifications_enabled(Boolean notifications_enabled) {
        this.notifications_enabled = notifications_enabled;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
