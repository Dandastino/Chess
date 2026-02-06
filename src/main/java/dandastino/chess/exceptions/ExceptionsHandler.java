package dandastino.chess.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ErrorDTO handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return new ErrorDTO(
                "You are not authorized to access this resource.",
                LocalDateTime.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorDTO handleMissingBody(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        
        // Check for JSON parsing errors
        if (message != null) {
            // Missing required field
            if (message.contains("required") || message.contains("missing")) {
                return new ErrorDTO(
                        "Required field is missing. For creating a game, you need: whitePlayerId (UUID), blackPlayerId (UUID). Optional: timeControl (String), initialFen (String), isBotGame (boolean), botDifficulty (int)",
                        LocalDateTime.now());
            }
            
            // Invalid UUID format
            if (message.contains("UUID") || message.contains("cannot deserialize")) {
                return new ErrorDTO(
                        "Invalid field format. Ensure whitePlayerId and blackPlayerId are valid UUIDs (e.g., '29b5e8b0-7d9c-4783-803f-53be19ca1a5b')",
                        LocalDateTime.now());
            }
            
            // Trailing comma or malformed JSON
            if (message.contains("Unexpected character") || message.contains("was expecting")) {
                return new ErrorDTO(
                        "Malformed JSON. Remove any trailing commas and ensure proper JSON syntax.",
                        LocalDateTime.now());
            }
        }
        
        return new ErrorDTO(
                "Invalid request body. Ensure Content-Type is set to 'application/json' and the request body contains valid JSON.",
                LocalDateTime.now());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorsDTO handleValidationException(ValidationException ex) {
        return new ErrorsDTO(ex.getMessage(), LocalDateTime.now(), ex.getErrors());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public ErrorDTO handleNotFoundException(NotFoundException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorDTO handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        
        if (message != null && (message.contains("unique constraint") || message.contains("Duplicate entry"))) {
            // Context-aware error message based on table
            if (message.contains("users")) {
                return new ErrorDTO("This username or email already exists.", LocalDateTime.now());
            } else if (message.contains("games")) {
                return new ErrorDTO("This game already exists or contains invalid data.", LocalDateTime.now());
            } else if (message.contains("game_states")) {
                return new ErrorDTO("A game state with this configuration already exists.", LocalDateTime.now());
            } else if (message.contains("moves")) {
                return new ErrorDTO("This move is invalid or already exists.", LocalDateTime.now());
            } else if (message.contains("messages")) {
                return new ErrorDTO("This message could not be stored due to invalid data.", LocalDateTime.now());
            }
            return new ErrorDTO("Duplicate or conflicting data. Please verify your input.", LocalDateTime.now());
        }
        
        if (message != null && message.contains("not-null constraint")) {
            // Try to extract the column name
            String columnName = extractColumnName(message);
            return new ErrorDTO("Missing required field: " + columnName, LocalDateTime.now());
        }
        
        return new ErrorDTO("Invalid data provided. Please check your request.", LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).toList();
        return new ErrorsDTO("Validation failed", LocalDateTime.now(), errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleIllegalArgument(IllegalArgumentException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorDTO handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return new ErrorDTO("Uploaded file is too large", LocalDateTime.now());
    }

    @ExceptionHandler(MailException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorDTO handleMailException(MailException ex) {
        return new ErrorDTO("Email sending failed: " + ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(dandastino.chess.exceptions.UploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleUploadException(dandastino.chess.exceptions.UploadException ex) {
        return new ErrorDTO("File upload failed: " + ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDTO handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
    public ErrorDTO handleMissingRequestHeader(MissingRequestHeaderException ex) {
        return new ErrorDTO("Missing Authorization header", LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorDTO handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ErrorDTO("Invalid parameter: " + ex.getName(), LocalDateTime.now());
    }

    private String extractColumnName(String errorMessage) {
        // Pattern: 'column_name' of relation
        int start = errorMessage.indexOf("\"");
        if (start != -1) {
            int end = errorMessage.indexOf("\"", start + 1);
            if (end != -1) {
                return errorMessage.substring(start + 1, end);
            }
        }
        return "unknown";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ErrorDTO handleGeneralException(Exception ex) {
        ex.printStackTrace();
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        return new ErrorDTO("Internal server error: " + message, LocalDateTime.now());
    }

    @ExceptionHandler(UnauthorizeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) //401
    public ErrorDTO handleUnauthorizeException(Exception ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public ErrorDTO handleForbiddenException(ForbiddenException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorDTO handleBadRequestException(BadRequestException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AlreadyExists.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorDTO handleAlreadyExists(AlreadyExists ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorDTO handleConflictException(ConflictException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }
}
