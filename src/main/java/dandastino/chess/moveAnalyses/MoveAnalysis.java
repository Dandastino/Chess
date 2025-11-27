package dandastino.chess.moveAnalyses;

import dandastino.chess.moves.Move;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="move_analyses")
public class MoveAnalysis {

    @Id
    @GeneratedValue
    private UUID move_analysis_id;
    private String evaluation_cp;
    private String best_move;
    private int deph;
    @Enumerated(EnumType.STRING)
    private Review review;

    @OneToOne(mappedBy = "moveAnalysis")
    private Move move;


    public MoveAnalysis(){}

    public MoveAnalysis(Move move, Review review, int deph, String best_move, String evaluation_cp) {
        this.move = move;
        this.review = review;
        this.deph = deph;
        this.best_move = best_move;
        this.evaluation_cp = evaluation_cp;
    }

    public UUID getMove_analysis_id() {
        return move_analysis_id;
    }

    public String getEvaluation_cp() {
        return evaluation_cp;
    }

    public void setEvaluation_cp(String evaluation_cp) {
        this.evaluation_cp = evaluation_cp;
    }

    public String getBest_move() {
        return best_move;
    }

    public void setBest_move(String best_move) {
        this.best_move = best_move;
    }

    public int getDeph() {
        return deph;
    }

    public void setDeph(int deph) {
        this.deph = deph;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return "MoveAnalysis{" +
                "move_analysis_id=" + move_analysis_id +
                ", evaluation_cp='" + evaluation_cp + '\'' +
                ", best_move='" + best_move + '\'' +
                ", deph=" + deph +
                ", review=" + review +
                ", move=" + move +
                '}';
    }


}
