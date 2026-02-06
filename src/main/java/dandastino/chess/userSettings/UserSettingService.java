package dandastino.chess.userSettings;

import dandastino.chess.exceptions.NotFoundException;
import dandastino.chess.exceptions.UnauthorizeException;
import dandastino.chess.users.User;
import dandastino.chess.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserSettingService {

    @Autowired
    private UsersSettingsRepository usersSettingsRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<UserSettingResponseDTO> getAllUserSettings() {
        return usersSettingsRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public UserSettingResponseDTO getUserSettingById(UUID userSettingId) {
        UserSetting userSetting = usersSettingsRepository.findById(userSettingId)
                .orElseThrow(() -> new NotFoundException(userSettingId));
        return convertToDTO(userSetting);
    }

    public UserSettingResponseDTO getUserSettingByUserId(UUID userId) {
        UserSetting userSetting = usersSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User settings not found for user " + userId));
        return convertToDTO(userSetting);
    }

    public UserSettingResponseDTO createUserSetting(UUID userId, UserSettingDTO userSettingDTO) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserSetting userSetting = new UserSetting();
        userSetting.setUser(user);
        userSetting.setTheme(userSettingDTO.theme());
        userSetting.setPreferred_time_control(userSettingDTO.preferredTimeControl());
        userSetting.setLanguage(userSettingDTO.language());
        userSetting.setAllow_engine_analysis(userSettingDTO.allowEngineAnalysis());
        userSetting.setShow_move_suggestions(userSettingDTO.showMoveSuggestions());
        userSetting.setNotifications_enabled(userSettingDTO.notificationsEnabled());
        userSetting.setCreated_at(LocalDateTime.now());

        UserSetting saved = usersSettingsRepository.save(userSetting);
        return convertToDTO(saved);
    }

    public UserSettingResponseDTO updateUserSetting(UUID userId, UserSettingDTO userSettingDTO) {
        UserSetting userSetting = usersSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User settings not found"));

        if (userSettingDTO.theme() != null) {
            userSetting.setTheme(userSettingDTO.theme());
        }
        if (userSettingDTO.preferredTimeControl() != null) {
            userSetting.setPreferred_time_control(userSettingDTO.preferredTimeControl());
        }
        if (userSettingDTO.language() != null) {
            userSetting.setLanguage(userSettingDTO.language());
        }
        userSetting.setAllow_engine_analysis(userSettingDTO.allowEngineAnalysis());
        userSetting.setShow_move_suggestions(userSettingDTO.showMoveSuggestions());
        userSetting.setNotifications_enabled(userSettingDTO.notificationsEnabled());

        UserSetting saved = usersSettingsRepository.save(userSetting);
        return convertToDTO(saved);
    }

    public void deleteUserSetting(UUID userSettingId, User currentUser) {
        UserSetting userSetting = usersSettingsRepository.findById(userSettingId)
                .orElseThrow(() -> new NotFoundException(userSettingId));
        if (userSetting.getUser() == null || !userSetting.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizeException("You can only delete your own settings");
        }
        usersSettingsRepository.delete(userSetting);
    }

    private UserSettingResponseDTO convertToDTO(UserSetting userSetting) {
        return new UserSettingResponseDTO(
                userSetting.getUser_setting_id(),
                userSetting.getUser() != null ? userSetting.getUser().getUser_id() : null,
                userSetting.getTheme(),
                userSetting.getPreferred_time_control(),
                userSetting.getLanguage(),
                userSetting.isAllow_engine_analysis(),
                userSetting.isShow_move_suggestions(),
                userSetting.isNotifications_enabled(),
                userSetting.getCreated_at()
        );
    }
}
