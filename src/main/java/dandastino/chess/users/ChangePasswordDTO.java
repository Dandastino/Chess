package dandastino.chess.users;

public record ChangePasswordDTO(
        String oldPassword,
        String newPassword
) {
}
