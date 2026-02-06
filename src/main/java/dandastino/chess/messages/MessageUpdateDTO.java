package dandastino.chess.messages;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateDTO(
        @NotBlank(message = "Content is required")
        String content
) {}
