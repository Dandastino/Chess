package dandastino.chess.user_settings;

import dandastino.chess.users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name="user_settings")
@Entity
public class UserSetting {

    @Id
    @GeneratedValue
    private UUID userSetting;
    private String theme;
    private String preferred_time_control;
    private String language;
    private boolean allow_engine_analysis;
    private boolean show_move_suggestions;
    private boolean notifications_enabled;
    private LocalDateTime created_at;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserSetting(){}

    public UserSetting(LocalDateTime created_at, boolean notifications_enabled, boolean show_move_suggestions, boolean allow_engine_analysis, String language, String preferred_time_control, String theme, UUID userSetting1) {
        this.created_at = created_at;
        this.notifications_enabled = true;
        this.show_move_suggestions = true;
        this.allow_engine_analysis = true;
        this.language = language;
        this.preferred_time_control = preferred_time_control;
        this.theme = theme;
        this.userSetting = userSetting1;
    }

    public UUID getUserSetting() {
        return userSetting;
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

    public boolean isAllow_engine_analysis() {
        return allow_engine_analysis;
    }

    public void setAllow_engine_analysis(boolean allow_engine_analysis) {
        this.allow_engine_analysis = allow_engine_analysis;
    }

    public boolean isShow_move_suggestions() {
        return show_move_suggestions;
    }

    public void setShow_move_suggestions(boolean show_move_suggestions) {
        this.show_move_suggestions = show_move_suggestions;
    }

    public boolean isNotifications_enabled() {
        return notifications_enabled;
    }

    public void setNotifications_enabled(boolean notifications_enabled) {
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
