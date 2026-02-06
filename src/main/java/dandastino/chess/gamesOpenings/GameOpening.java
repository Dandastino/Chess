package dandastino.chess.gamesOpenings;

import dandastino.chess.games.Game;
import dandastino.chess.openings.Opening;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="games_openings")
public class GameOpening {

    @Id
    @GeneratedValue
    @Column(name = "game_openings_id")
    private UUID game_opening_id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "opening_id")
    private Opening opening;

    public GameOpening(){}

    public GameOpening(Opening opening, Game game) {
        this.opening = opening;
        this.game = game;
    }

    public UUID getGame_opening_id() {
        return game_opening_id;
    }

    public void setGame_opening_id(UUID game_opening_id) {
        this.game_opening_id = game_opening_id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Opening getOpening() {
        return opening;
    }

    public void setOpening(Opening opening) {
        this.opening = opening;
    }

    @Override
    public String toString() {
        return "GameOpening{" +
                "game_opening_id=" + game_opening_id +
                ", game=" + game +
                ", opening=" + opening +
                '}';
    }
}
