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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cheating_analisys_id")
    private UUID cheating_analysis_id;
    @Column(name = "match_accuracy_percentage")
    private double match_accuracy_perc;
    @Column(name = "suspicion_score")
    private double suspicion_score;
    @Column(name = "created_at")
    private LocalDateTime created_at;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game cheating_game;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User cheating_user;

    public CheatingAnalysis(){}

    public CheatingAnalysis(UUID cheating_analysis_id, double match_accuracy_perc, double suspicion_score, LocalDateTime created_at, Game cheating_game, User cheating_user) {
        this.cheating_analysis_id = cheating_analysis_id;
        this.match_accuracy_perc = match_accuracy_perc;
        this.suspicion_score = suspicion_score;
        this.created_at = created_at;
        this.cheating_game = cheating_game;
        this.cheating_user = cheating_user;
    }

    public UUID getCheating_analysis_id() {
        return cheating_analysis_id;
    }

    public void setCheating_analysis_id(UUID cheating_analysis_id) {
        this.cheating_analysis_id = cheating_analysis_id;
    }

    public double getMatch_accuracy_perc() {
        return match_accuracy_perc;
    }

    public void setMatch_accuracy_perc(double match_accuracy_perc) {
        this.match_accuracy_perc = match_accuracy_perc;
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
                ", match_accuracy_perc=" + match_accuracy_perc +
                ", suspicion_score=" + suspicion_score +
                ", created_at=" + created_at +
                ", cheating_game=" + cheating_game +
                ", cheating_user=" + cheating_user +
                '}';
    }
}

