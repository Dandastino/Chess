package dandastino.chess.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDTO(
        @NotBlank(message = "Recipient email is required") @Email(message = "Invalid recipient email") String to,
        @NotBlank(message = "Subject is required") String subject,
        @NotBlank(message = "Body is required") String body
) {}
