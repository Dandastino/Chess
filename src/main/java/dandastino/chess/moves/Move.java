package dandastino.chess.moves;

import dandastino.chess.games.Game;
import dandastino.chess.moveAnalyses.MoveAnalysis;
import dandastino.chess.users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="moves")
public class Move {

    @Id
    @GeneratedValue
    private UUID move_id;
    private int move_number;
    private String san_move;
    private String from_square;
    private String to_square;
    private String fen_after_move;
    private LocalDateTime timestamp;
    private int time_spent_ms;
    private boolean is_check;
    private boolean is_checkmate;

    @OneToOne
    @JoinColumn(name = "move_analysis_id")
    private MoveAnalysis moveAnalysis;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game gameAnalysis;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userMove;

    public Move(){}

    public Move(int move_number, String san_move, String from_square, String to_square, String fen_after_move, LocalDateTime timestamp, int time_spent_ms, boolean is_check, boolean is_checkmate, MoveAnalysis moveAnalysis, Game gameAnalysis, User userAnalysis) {
        this.move_number = move_number;
        this.san_move = san_move;
        this.from_square = from_square;
        this.to_square = to_square;
        this.fen_after_move = fen_after_move;
        this.timestamp = timestamp;
        this.time_spent_ms = time_spent_ms;
        this.is_check = false;
        this.is_checkmate = false;
        this.moveAnalysis = moveAnalysis;
        this.gameAnalysis = gameAnalysis;
        this.userMove = userAnalysis;
    }


    public UUID getMove_id() {
        return move_id;
    }

    public int getMove_number() {
        return move_number;
    }

    public void setMove_number(int move_number) {
        this.move_number = move_number;
    }

    public String getSan_move() {
        return san_move;
    }

    public void setSan_move(String san_move) {
        this.san_move = san_move;
    }

    public String getFrom_square() {
        return from_square;
    }

    public void setFrom_square(String from_square) {
        this.from_square = from_square;
    }

    public String getTo_square() {
        return to_square;
    }

    public void setTo_square(String to_square) {
        this.to_square = to_square;
    }

    public String getFen_after_move() {
        return fen_after_move;
    }

    public void setFen_after_move(String fen_after_move) {
        this.fen_after_move = fen_after_move;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getTime_spent_ms() {
        return time_spent_ms;
    }

    public void setTime_spent_ms(int time_spent_ms) {
        this.time_spent_ms = time_spent_ms;
    }

    public boolean isIs_check() {
        return is_check;
    }

    public void setIs_check(boolean is_check) {
        this.is_check = is_check;
    }

    public boolean isIs_checkmate() {
        return is_checkmate;
    }

    public void setIs_checkmate(boolean is_checkmate) {
        this.is_checkmate = is_checkmate;
    }

    public MoveAnalysis getMoveAnalysis() {
        return moveAnalysis;
    }

    public void setMoveAnalysis(MoveAnalysis moveAnalysis) {
        this.moveAnalysis = moveAnalysis;
    }

    public Game getGameAnalysis() {
        return gameAnalysis;
    }

    public void setGameAnalysis(Game gameAnalysis) {
        this.gameAnalysis = gameAnalysis;
    }

    public User getUserAnalysis() {
        return userMove;
    }

    public void setUserAnalysis(User userAnalysis) {
        this.userMove = userAnalysis;
    }

    @Override
    public String toString() {
        return "Move{" +
                "move_id=" + move_id +
                ", move_number=" + move_number +
                ", san_move='" + san_move + '\'' +
                ", from_square='" + from_square + '\'' +
                ", to_square='" + to_square + '\'' +
                ", fen_after_move='" + fen_after_move + '\'' +
                ", timestamp=" + timestamp +
                ", time_spent_ms=" + time_spent_ms +
                ", is_check=" + is_check +
                ", is_checkmate=" + is_checkmate +
                ", moveAnalysis=" + moveAnalysis +
                ", gameAnalysis=" + gameAnalysis +
                ", userAnalysis=" + userMove +
                '}';
    }

    public User getUserMove() {
        return userMove;
    }

    public void setUserMove(User userMove) {
        this.userMove = userMove;
    }
}
