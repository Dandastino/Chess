package dandastino.chess.cheatingAnalyses;

import dandastino.chess.games.Game;
import dandastino.chess.users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="cheating_analyses")
public class CheatingAnalysis {

    @Id
    @GeneratedValue
    private UUID cheating_analysis_id;
    private double match_accuracy_percentage;
    private double suspicion_score;
    private LocalDateTime created_at;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game cheating_game;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User cheating_user;

    public CheatingAnalysis(){}

    public CheatingAnalysis(UUID cheating_analysis_id, double match_accuracy_percentage, double suspicion_score, LocalDateTime created_at, Game cheating_game, User cheating_user) {
        this.cheating_analysis_id = cheating_analysis_id;
        this.match_accuracy_percentage = match_accuracy_percentage;
        this.suspicion_score = suspicion_score;
        this.created_at = created_at;
        this.cheating_game = cheating_game;
        this.cheating_user = cheating_user;
    }

    public UUID getCheating_analysis_id() {
        return cheating_analysis_id;
    }

    public double getMatch_accuracy_percentage() {
        return match_accuracy_percentage;
    }

    public void setMatch_accuracy_percentage(double match_accuracy_percentage) {
        this.match_accuracy_percentage = match_accuracy_percentage;
    }

    public double getSuspicion_score() {
        return suspicion_score;
    }

    public void setSuspicion_score(double suspicion_score) {
        this.suspicion_score = suspicion_score;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Game getCheating_game() {
        return cheating_game;
    }

    public void setCheating_game(Game cheating_game) {
        this.cheating_game = cheating_game;
    }

    public User getCheating_user() {
        return cheating_user;
    }

    public void setCheating_user(User cheating_user) {
        this.cheating_user = cheating_user;
    }

    @Override
    public String toString() {
        return "CheatingAnalysis{" +
                "cheating_analysis_id=" + cheating_analysis_id +
                ", match_accuracy_percentage=" + match_accuracy_percentage +
                ", suspicion_score=" + suspicion_score +
                ", created_at=" + created_at +
                ", cheating_game=" + cheating_game +
                ", cheating_user=" + cheating_user +
                '}';
    }
}

