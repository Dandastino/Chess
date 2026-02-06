package dandastino.chess.users;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dandastino.chess.exceptions.AlreadyExists;
import dandastino.chess.exceptions.BadRequestException;
import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.utility.Country;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    private UsersRepository users_repo;
    @Autowired
    private Cloudinary imageUploader;
    @Autowired
    private PasswordEncoder bcrypt;

    public List<User> getUsers() {
        return this.users_repo.findAll();
    }

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final int MIN_ELO_RATING = 0;
    private static final int MAX_ELO_RATING = 3200;

    public NewUsersRespDTO createUser(NewUsersDTO body){
        if(usernameExists(body.username())) throw new AlreadyExists(body.username());
        if(emailExists(body.email())) throw new AlreadyExists(body.email());

        LocalDateTime createdAt = body.created_at() != null ? body.created_at() : LocalDateTime.now();
        User newUser = new User(body.bio(), body.avatar_url(), createdAt, body.elo_rating(), bcrypt.encode(body.password()), body.email(), body.username(), body.country());
        logger.debug("Creating user - email: {}, username: {}, passwordHash: {}, country: {}, createdAt: {}",
            newUser.getEmail(), newUser.getUsername(), newUser.getPassword(), newUser.getCountry(), newUser.getCreated_at());
        User saved = this.users_repo.save(newUser);

        return new NewUsersRespDTO(saved.getId());
    }

    public User getUserById(UUID userId){
        return this.users_repo.findById(userId).orElseThrow(() -> new NotFoundException(userId));
    }

    public User getUserByUsername(String username){
        return this.users_repo.findByUsername(username).orElseThrow(() -> new NotFoundException(username));
    }

    public User updateUser(UUID userId, NewUsersDTO body){
        User found = getUserById(userId);

        if(usernameExists(body.username())) throw new AlreadyExists(body.username());
        if(emailExists(body.email())) throw new AlreadyExists(body.email());

        found.setBio(body.bio());
        found.setAvatar_url(body.avatar_url());
        found.setElo_rating(body.elo_rating());
        found.setPassword(bcrypt.encode(body.password()));
        found.setEmail(body.email());
        found.setUsername(body.username());

        return this.users_repo.save(found);
    }

    public User partialUpdateUser(UUID userId, String bio, String username, String email, Country country, Integer elo_rating) {
        User found = getUserById(userId);
        logger.debug("PATCH request - bio: {}, username: {}, email: {}, country: {}, elo_rating: {}", bio, username, email, country, elo_rating);

        // Only update fields that are provided (not null)
        if (bio != null) {
            validateBio(bio);
            logger.debug("Updating bio to: {}", bio);
            found.setBio(bio);
        }
        if (username != null) {
            validateUsername(username);
            if (!username.equals(found.getUsername()) && usernameExists(username)) {
                throw new AlreadyExists("Username '" + username + "' already exists");
            }
            logger.debug("Updating username to: {}", username);
            found.setUsername(username);
        }
        if (email != null) {
            validateEmail(email);
            if (!email.equals(found.getEmail()) && emailExists(email)) {
                throw new AlreadyExists("Email '" + email + "' already exists");
            }
            logger.debug("Updating email to: {}", email);
            found.setEmail(email);
        }
        if (country != null) {
            logger.debug("Updating country to: {}", country);
            found.setCountry(country);
        }
        if (elo_rating != null) {
            validateEloRating(elo_rating);
            logger.debug("Updating elo_rating to: {}", elo_rating);
            found.setElo_rating(elo_rating);
        }

        logger.debug("Saving user: {}", found.getUsername());
        return this.users_repo.save(found);
    }

    private void validateBio(String bio) {
        if (bio.trim().isEmpty()) {
            throw new BadRequestException("Bio cannot be empty");
        }
        if (bio.length() > 500) {
            throw new BadRequestException("Bio cannot exceed 500 characters");
        }
    }

    private void validateUsername(String username) {
        if (username.trim().isEmpty()) {
            throw new BadRequestException("Username cannot be empty");
        }
        if (username.length() < 3) {
            throw new BadRequestException("Username must be at least 3 characters");
        }
        if (username.length() > 50) {
            throw new BadRequestException("Username cannot exceed 50 characters");
        }
        if (!username.matches("^[a-zA-Z0-9_-]+$")) {
            throw new BadRequestException("Username can only contain letters, numbers, underscores, and hyphens");
        }
    }

    private void validateEmail(String email) {
        if (email.trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Email format is invalid");
        }
        if (email.length() > 100) {
            throw new BadRequestException("Email cannot exceed 100 characters");
        }
    }

    private void validateEloRating(Integer elo_rating) {
        if (elo_rating < MIN_ELO_RATING || elo_rating > MAX_ELO_RATING) {
            throw new BadRequestException("Elo rating must be between " + MIN_ELO_RATING + " and " + MAX_ELO_RATING);
        }
    }

    public void deleteUser(UUID userId){
        User found = getUserById(userId);
        this.users_repo.delete(found);
    }

    public void updateUserUrl(UUID userId, MultipartFile file){
        User found = getUserById(userId);
        validateFile(file);

        Map<String, Object> options = ObjectUtils.asMap("folder", "chess/avatars");

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) imageUploader.uploader().upload(file.getBytes(), options);
            String imageUrl = result.get("secure_url").toString();
            found.setAvatar_url(imageUrl);
            this.users_repo.save(found);
        } catch (IOException e) {
            throw new dandastino.chess.exceptions.UploadException("Failed to upload avatar image", e);
        }

    }

    public User findByUsername(String username) {
        return this.users_repo.findByUsername(username).orElseThrow(() -> new NotFoundException("The user with username " + username + "is does not exist "));
    }

    public boolean usernameExists(String username) {
        return this.users_repo.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return this.users_repo.existsByEmail(email);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("the file can't  be empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ValidationException("the file has a limit of: " + (MAX_FILE_SIZE_BYTES / 1024 / 1024) + " MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new ValidationException("the file format is not allowed pls inset a: JPEG, PNG or GIF.");
        }
    }

    public List<LeaderboardResponseDTO> getLeaderboard(int limit) {
        return this.users_repo.findAll().stream()
                .sorted((u1, u2) -> Integer.compare(u2.getElo_rating(), u1.getElo_rating()))
                .limit(limit)
                .map(u -> new LeaderboardResponseDTO(u.getUsername(), u.getElo_rating()))
                .toList();
    }

    public Object getUserStats(UUID userId) {
        User user = getUserById(userId);
        // Returns basic stats object - can be extended with game statistics
        return Map.of(
            "userId", user.getUser_id(),
            "username", user.getUsername(),
            "eloRating", user.getElo_rating(),
            "createdAt", user.getCreated_at()
        );
    }

    public void changePassword(UUID userId, ChangePasswordDTO passwordChange) {
        User user = getUserById(userId);
        
        // Validate old password
        if (passwordChange.oldPassword() == null || passwordChange.oldPassword().trim().isEmpty()) {
            throw new BadRequestException("Current password is required");
        }
        
        if (!bcrypt.matches(passwordChange.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Validate new password
        validateNewPassword(passwordChange.newPassword());
        
        // New password cannot be same as old password
        if (bcrypt.matches(passwordChange.newPassword(), user.getPassword())) {
            throw new BadRequestException("New password cannot be the same as current password");
        }
        
        // Update password
        logger.debug("Changing password for user: {}", user.getUsername());
        user.setPassword(bcrypt.encode(passwordChange.newPassword()));
        this.users_repo.save(user);
    }

    private void validateNewPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("New password is required");
        }
        if (password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long");
        }
        if (password.length() > 100) {
            throw new BadRequestException("Password cannot exceed 100 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BadRequestException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new BadRequestException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BadRequestException("Password must contain at least one digit");
        }
    }

}
