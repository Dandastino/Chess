package dandastino.chess.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(UUID id) {
        super("the resource with id " + id + " was not found");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
