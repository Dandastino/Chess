package dandastino.chess.moves;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.games.Game;
import dandastino.chess.games.GamesRepository;
import dandastino.chess.games.Status;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersRepository;
import dandastino.chess.websocket.GameBroadcastService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MoveService {
    private static final Logger logger = LoggerFactory.getLogger(MoveService.class);
    
    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private MovesRepository movesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private GameBroadcastService broadcastService;

    public List<MoveResponseDTO> getAllMoves() {
        return movesRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public MoveResponseDTO getMoveById(UUID moveId) {
        Move move = movesRepository.findById(moveId)
                .orElseThrow(() -> new NotFoundException(moveId));
        return convertToDTO(move);
    }

    public MoveResponseDTO createMove(MoveRequestDTO moveDTO) {
        Game game = gamesRepository.findById(moveDTO.gameId())
                .orElseThrow(() -> new NotFoundException("Game not found"));
        User player = usersRepository.findById(moveDTO.playerId())
                .orElseThrow(() -> new NotFoundException("Player not found"));

        Move move = new Move();
        move.setGameAnalysis(game);
        move.setUserMove(player);
        move.setMoveNumber(moveDTO.moveNumber());
        move.setSanMove(moveDTO.sanMove());
        move.setFromSquare(moveDTO.fromSquare());
        move.setToSquare(moveDTO.toSquare());
        move.setStartRow(moveDTO.startRow());
        move.setStartCol(moveDTO.startCol());
        move.setEndRow(moveDTO.endRow());
        move.setEndCol(moveDTO.endCol());
        move.setFenAfterMove(moveDTO.fenAfterMove());
        move.setTimestamp(LocalDateTime.now());
        move.setTimeSpentMs(moveDTO.timeSpentMs());
        move.setCheck(moveDTO.isCheck());
        move.setCheckmate(moveDTO.isCheckmate());

        Move saved = movesRepository.save(move);
        
        // If checkmate, automatically set game status to done
        if (moveDTO.isCheckmate()) {
            logger.info("Checkmate detected in game {}. Changing status to done.", game.getGame_id());
            game.setStatus(Status.done);
            gamesRepository.save(game);
        }
        
        MoveResponseDTO responseDTO = convertToDTO(saved);
        
        // Broadcast the move to all connected players via WebSocket
        broadcastService.broadcastMove(responseDTO);
        
        return responseDTO;
    }

    public MoveResponseDTO updateMove(UUID moveId, MoveRequestDTO moveDTO) {
        Move move = movesRepository.findById(moveId)
                .orElseThrow(() -> new NotFoundException(moveId));

        if (moveDTO.sanMove() != null) {
            move.setSanMove(moveDTO.sanMove());
        }
        if (moveDTO.fenAfterMove() != null) {
            move.setFenAfterMove(moveDTO.fenAfterMove());
        }
        if (moveDTO.timeSpentMs() > 0) {
            move.setTimeSpentMs(moveDTO.timeSpentMs());
        }
        move.setCheck(moveDTO.isCheck());
        move.setCheckmate(moveDTO.isCheckmate());

        Move saved = movesRepository.save(move);
        return convertToDTO(saved);
    }

    public void deleteMove(UUID moveId) {
        Move move = movesRepository.findById(moveId)
                .orElseThrow(() -> new NotFoundException(moveId));
        movesRepository.delete(move);
    }

    public List<MoveResponseDTO> getMovesByGame(UUID gameId) {
        return movesRepository.findByGameId(gameId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<MoveResponseDTO> getMovesByPlayer(UUID playerId) {
        return movesRepository.findByPlayerId(playerId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private MoveResponseDTO convertToDTO(Move move) {
        return new MoveResponseDTO(
                move.getMoveId(),
                move.getGameAnalysis() != null ? move.getGameAnalysis().getGame_id() : null,
                move.getUserMove() != null ? move.getUserMove().getUser_id() : null,
                move.getMoveNumber(),
                move.getSanMove(),
                move.getFromSquare(),
                move.getToSquare(),
                move.getStartRow(),
                move.getStartCol(),
                move.getEndRow(),
                move.getEndCol(),
                move.getFenAfterMove(),
                move.getTimestamp(),
                move.getTimeSpentMs(),
                move.isCheck(),
                move.isCheckmate()
        );
    }
}
