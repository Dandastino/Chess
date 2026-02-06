package dandastino.chess.moves;

import dandastino.chess.games.Game;
import dandastino.chess.moveAnalyses.MoveAnalysis;
import dandastino.chess.piece.PieceType;
import dandastino.chess.users.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="moves")
public class Move {

    @Id
    @GeneratedValue
    @Column(name = "move_id")
    private UUID moveId;
    @Column(name = "move_number")
    private int moveNumber;
    @Column(name = "san_move")
    private String sanMove;
    @Column(name = "from_square")
    private String fromSquare;
    @Column(name = "to_square")
    private String toSquare;
    private int startRow;
    private int endRow;
    private int startCol;
    private int endCol;
    @Enumerated(EnumType.STRING)
    private PieceType pieceType;
    @Column(name = "fen_after_move")
    private String fenAfterMove;
    private LocalDateTime timestamp;
    @Column(name = "time_spent_ms")
    private int timeSpentMs;
    @Column(name = "is_check")
    private boolean isCheck;
    @Column(name = "is_checkmate")
    private boolean isCheckmate;

    @OneToOne
    @JoinColumn(name = "move_analysis_id")
    private MoveAnalysis moveAnalysis;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game gameAnalysis;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private User userMove;

    public Move(){}

    public Move(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
    }

    public User getUserMove() {
        return userMove;
    }

    public void setUserMove(User userMove) {
        this.userMove = userMove;
    }

    public UUID getMoveId() {
        return moveId;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public String getSanMove() {
        return sanMove;
    }

    public void setSanMove(String sanMove) {
        this.sanMove = sanMove;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public String getFenAfterMove() {
        return fenAfterMove;
    }

    public void setFenAfterMove(String fenAfterMove) {
        this.fenAfterMove = fenAfterMove;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimeSpentMs() {
        return timeSpentMs;
    }

    public void setTimeSpentMs(int timeSpentMs) {
        this.timeSpentMs = timeSpentMs;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean checkmate) {
        isCheckmate = checkmate;
    }

    public String getFromSquare() {
        return fromSquare;
    }

    public void setFromSquare(String fromSquare) {
        this.fromSquare = fromSquare;
    }

    public String getToSquare() {
        return toSquare;
    }

    public void setToSquare(String toSquare) {
        this.toSquare = toSquare;
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
}
