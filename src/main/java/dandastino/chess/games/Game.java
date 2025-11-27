package dandastino.chess.games;

import dandastino.chess.cheatingAnalyses.CheatingAnalysis;
import dandastino.chess.users.User;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="games")
public class Game {

    @Id
    @GeneratedValue
    private UUID game_id;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    @Enumerated(EnumType.STRING)
    private Result result;
    private String time_control;
    private String initital_fen;
    private String final_fen;
    private boolean is_bot_game;
    private int botDifficulty;
    @ManyToOne
    @JoinColumn(name = "white_id", nullable = false)
    private User whitePlayer;
    @ManyToOne
    @JoinColumn(name = "black_id", nullable = false)
    private User blackPlayer;
    @ManyToOne
    @JoinColumn(name = "winner_id", nullable = true)
    private User winner;

    @OneToMany(mappedBy = "cheating_game")
    private List<CheatingAnalysis> cheating_game;

    public Game(){}

    public Game(UUID game_id, Status status, LocalDateTime created_at, LocalDateTime updated_at, Result result, String time_control, String initital_fen, String final_fen, boolean is_bot_game, int botDifficulty, User whitePlayer, User blackPlayer, User winner) {
        this.game_id = game_id;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.result = result;
        this.time_control = time_control;
        this.initital_fen = initital_fen;
        this.final_fen = final_fen;
        this.is_bot_game = is_bot_game;
        this.botDifficulty = botDifficulty;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.winner = winner;
    }

    public UUID getGame_id() {
        return game_id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getTime_control() {
        return time_control;
    }

    public void setTime_control(String time_control) {
        this.time_control = time_control;
    }

    public String getInitital_fen() {
        return initital_fen;
    }

    public void setInitital_fen(String initital_fen) {
        this.initital_fen = initital_fen;
    }

    public String getFinal_fen() {
        return final_fen;
    }

    public void setFinal_fen(String final_fen) {
        this.final_fen = final_fen;
    }

    public boolean isIs_bot_game() {
        return is_bot_game;
    }

    public void setIs_bot_game(boolean is_bot_game) {
        this.is_bot_game = is_bot_game;
    }

    public int getBotDifficulty() {
        return botDifficulty;
    }

    public void setBotDifficulty(int botDifficulty) {
        this.botDifficulty = botDifficulty;
    }

    public User getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(User whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public User getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(User blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "Game{" +
                "game_id=" + game_id +
                ", status=" + status +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", result=" + result +
                ", time_control='" + time_control + '\'' +
                ", initital_fen='" + initital_fen + '\'' +
                ", final_fen='" + final_fen + '\'' +
                ", is_bot_game=" + is_bot_game +
                ", botDifficulty=" + botDifficulty +
                ", whitePlayer=" + whitePlayer +
                ", blackPlayer=" + blackPlayer +
                ", winner=" + winner +
                '}';
    }
}
