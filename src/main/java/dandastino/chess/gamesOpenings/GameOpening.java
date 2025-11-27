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
    private UUID game_opening_id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "opening_id")
    private Opening opening;
}
