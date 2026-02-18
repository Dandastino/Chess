package dandastino.chess.moveAnalyses;

import dandastino.chess.moves.Move;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="move_analyses")
public class MoveAnalysis {

    @Id
    @GeneratedValue
    @Column(name = "move_analysis_id")
    private UUID move_analysis_id;
    @Column(name = "evaluation_cp")
    private String evaluation_cp;
    @Column(name = "best_move")
    private String best_move;
    @Column(name = "deph")
    private int depth;
    @Enumerated(EnumType.STRING)
    @Column(name = "review")
    private Review review;

    @OneToOne(mappedBy = "moveAnalysis")
    private Move move;


    public MoveAnalysis(){}

    public MoveAnalysis(Move move, Review review, int depth, String best_move, String evaluation_cp) {
        this.move = move;
        this.review = review;
        this.depth = depth;
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

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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
                ", depth=" + depth +
                ", review=" + review +
                ", move=" + move +
                '}';
    }


}
