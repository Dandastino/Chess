package dandastino.chess.openings;

import dandastino.chess.gamesOpenings.GameOpening;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="openings")
public class Opening {

    @Id
    @GeneratedValue
    @Column(name = "opening_id")
    private UUID opening_id;
    @Column(name = "name")
    private String name;
    @Column(name = "eco_code")
    private String eco_code;
    @Column(name = "fen_start")
    private String fen_start;
    @Column(name = "moves")
    private String moves;

    @OneToMany(mappedBy = "opening")
    private List<GameOpening> gamesOpenings;

    public Opening(){}

    public Opening(String name, String eco_code, String fen_start, String moves) {
        this.name = name;
        this.eco_code = eco_code;
        this.fen_start = fen_start;
        this.moves = moves;
    }

    public UUID getOpening_id() {
        return opening_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEco_code() {
        return eco_code;
    }

    public void setEco_code(String eco_code) {
        this.eco_code = eco_code;
    }

    public String getFen_start() {
        return fen_start;
    }

    public void setFen_start(String fen_start) {
        this.fen_start = fen_start;
    }

    public String getMoves() {
        return moves;
    }

    public void setMoves(String moves) {
        this.moves = moves;
    }

    @Override
    public String toString() {
        return "Opening{" +
                "opening_id=" + opening_id +
                ", name='" + name + '\'' +
                ", eco_code='" + eco_code + '\'' +
                ", fen_start='" + fen_start + '\'' +
                ", moves='" + moves + '\'' +
                '}';
    }

    public List<GameOpening> getGamesOpenings() {
        return gamesOpenings;
    }

    public void setGamesOpenings(List<GameOpening> gamesOpenings) {
        this.gamesOpenings = gamesOpenings;
    }
}
