package dandastino.chess.users;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import dandastino.chess.exceptions.AlreadyExists;
import dandastino.chess.exceptions.NotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UsersService {

    @Autowired
    private UsersRepository users_repo;
    @Autowired
    private Cloudinary imageUploader;

    public List<User> getUsers() {
        return this.users_repo.findAll();
    }

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/gif"
    );

    public NewUsersRespDTO createUser(NewUsersDTO body){
        if(usernameExists(body.username())) throw new AlreadyExists(body.username());
        if(emailExists(body.email())) throw new AlreadyExists(body.email());

        User newUser = new User(body.bio(), body.avatar_url(), body.created_at(), body.elo_rating(), body.password(), body.email(), body.username());
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
        found.setPassword(body.password());
        found.setEmail(body.email());
        found.setUsername(body.username());

        return this.users_repo.save(found);
    }

    public void deleteUser(UUID userId){
        User found = getUserById(userId);
        this.users_repo.delete(found);
    }

    public void updateUserUrl(UUID userId, MultipartFile file){
        User found = getUserById(userId);
        validateFile(file);

        Map options = ObjectUtils.asMap("folder", "chess/avatars");

        try {
            Map result = imageUploader.uploader().upload(file.getBytes(), options);
            String imageUrl = result.get("secure_url").toString();
            found.setAvatar_url(imageUrl);
            this.users_repo.save(found);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

}
