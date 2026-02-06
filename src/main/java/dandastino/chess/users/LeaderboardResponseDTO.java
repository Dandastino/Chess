package dandastino.chess.users;

public class LeaderboardResponseDTO {
    private String username;
    private Integer elo_rating;

    public LeaderboardResponseDTO(String username, Integer elo_rating) {
        this.username = username;
        this.elo_rating = elo_rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getElo_rating() {
        return elo_rating;
    }

    public void setElo_rating(Integer elo_rating) {
        this.elo_rating = elo_rating;
    }
}
