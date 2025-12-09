package dandastino.chess.moves;

import dandastino.chess.games.GamesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MoveService {
    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private MovesRepository movesRepository;




}
