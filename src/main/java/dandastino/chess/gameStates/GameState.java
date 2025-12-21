package dandastino.chess.gameStates;

import dandastino.chess.games.Game;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="game_states")
public class GameState {

    @Id
    @GeneratedValue
    @Column(name = "game_state_id")
    private UUID game_state_id;
    @Column(name = "move_number")
    private String move_number;
    @Column(name = "fen")
    private String fen;
    @Column(name = "evaluation_cp")
    private int evaluation_cp;
    @Column(name = "best_move")
    private String best_move;
    @Column(name = "analysis_json")
    private String analysis_json;
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public GameState(){}

    public GameState(String move_number, String fen, int evaluation_cp, String best_move, String analysis_json, Game game) {
        this.move_number = move_number;
        this.fen = fen;
        this.evaluation_cp = evaluation_cp;
        this.best_move = best_move;
        this.analysis_json = analysis_json;
        this.game = game;
    }

    public UUID getGame_id() {
        return game_state_id;
    }

    public String getMove_number() {
        return move_number;
    }

    public void setMove_number(String move_number) {
        this.move_number = move_number;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public int getEvaluation_cp() {
        return evaluation_cp;
    }

    public void setEvaluation_cp(int evaluation_cp) {
        this.evaluation_cp = evaluation_cp;
    }

    public String getBest_move() {
        return best_move;
    }

    public void setBest_move(String best_move) {
        this.best_move = best_move;
    }

    public String getAnalysis_json() {
        return analysis_json;
    }

    public void setAnalysis_json(String analysis_json) {
        this.analysis_json = analysis_json;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "game_state_id=" + game_state_id +
                ", move_number='" + move_number + '\'' +
                ", fen='" + fen + '\'' +
                ", evaluation_cp=" + evaluation_cp +
                ", best_move='" + best_move + '\'' +
                ", analysis_json='" + analysis_json + '\'' +
                '}';
    }


}