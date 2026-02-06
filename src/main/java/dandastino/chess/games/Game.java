package dandastino.chess.games;

import dandastino.chess.cheatingAnalyses.CheatingAnalysis;
import dandastino.chess.gameStates.GameState;
import dandastino.chess.gamesOpenings.GameOpening;
import dandastino.chess.messages.Message;
import dandastino.chess.users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="games")
public class Game {

    @Id
    @GeneratedValue
    @Column(name = "game_id")
    private UUID gameId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.waiting_for_player;
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private Result result;
    @Column(name = "time_control")
    private String timeControl;
    @Column(name = "initial_fen")
    private String initialFen;
    @Column(name = "final_fen")
    private String finalFen;
    @Column(name = "is_bot_game")
    private boolean isBotGame;
    @Column(name = "bot_difficulty")
    private int botDifficulty;

    @ManyToOne
    @JoinColumn(name = "white_player_id", nullable = true)
    private User whitePlayer;
    @ManyToOne
    @JoinColumn(name = "black_player_id", nullable = true)
    private User blackPlayer;
    @ManyToOne
    @JoinColumn(name = "winner_id", nullable = true)
    private User winner;
    @OneToMany(mappedBy = "game")
    private List<Message> messages;
    @OneToMany(mappedBy = "cheating_game")
    private List<CheatingAnalysis> cheatingGame;
    @OneToMany(mappedBy = "game")
    private List<GameState> gameStates;
    @OneToMany(mappedBy = "game")
    private List<GameOpening> gameOpenings;

    public Game(){}

    public Game(Status status, LocalDateTime createdAt, LocalDateTime finishedAt, Result result, String timeControl, String initialFen, String finalFen, boolean isBotGame, int botDifficulty, User whitePlayer, User blackPlayer, User winner) {
        this.status = status;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
        this.result = result;
        this.timeControl = timeControl;
        this.initialFen = initialFen;
        this.finalFen = finalFen;
        this.isBotGame = isBotGame;
        this.botDifficulty = botDifficulty;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.winner = winner;
    }

    public UUID getGame_id() {
        return gameId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getTime_control() {
        return timeControl;
    }

    public void setTimeControl(String timeControl) {
        this.timeControl = timeControl;
    }

    public String getInitialFen() {
        return initialFen;
    }

    public void setInitialFen(String initialFen) {
        this.initialFen = initialFen;
    }

    public String getFinalFen() {
        return finalFen;
    }

    public void setFinalFen(String finalFen) {
        this.finalFen = finalFen;
    }

    public boolean getIsBotGame() {
        return isBotGame;
    }

    public void setIsBotGame(boolean isBotGame) {
        this.isBotGame = isBotGame;
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
                "gameId=" + gameId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", finishedAt=" + finishedAt +
                ", result=" + result +
                ", timeControl='" + timeControl + '\'' +
                ", initialFen='" + initialFen + '\'' +
                ", finalFen='" + finalFen + '\'' +
                ", isBotGame=" + isBotGame +
                ", botDifficulty=" + botDifficulty +
                ", whitePlayer=" + whitePlayer +
                ", blackPlayer=" + blackPlayer +
                ", winner=" + winner +
                '}';
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<CheatingAnalysis> getCheatingGame() {
        return cheatingGame;
    }

    public List<GameState> getGameStates() {
        return gameStates;
    }

    public List<GameOpening> getGameOpenings() {
        return gameOpenings;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setCheating_game(List<CheatingAnalysis> cheating_game) {
        this.cheatingGame = cheating_game;
    }

    public void setGameStates(List<GameState> gameStates) {
        this.gameStates = gameStates;
    }

    public void setGameOpenings(List<GameOpening> gameOpenings) {
        this.gameOpenings = gameOpenings;
    }
}
