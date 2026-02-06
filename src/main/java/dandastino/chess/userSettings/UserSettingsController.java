package dandastino.chess.userSettings;

import dandastino.chess.exceptions.ValidationException;
import dandastino.chess.users.User;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-settings")
public class UserSettingsController {

    private final UserSettingService userSettingService;

    public UserSettingsController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @GetMapping
    public List<UserSettingResponseDTO> getAllUserSettings() {
        return userSettingService.getAllUserSettings();
    }

    @GetMapping("/{user_setting_id}")
    public UserSettingResponseDTO getUserSettingById(@PathVariable("user_setting_id") UUID userSettingId) {
        return userSettingService.getUserSettingById(userSettingId);
    }

    @GetMapping("/user/{user_id}")
    @PreAuthorize("#userId == authentication.principal.user_id")
    public UserSettingResponseDTO getUserSettingByUserId(@PathVariable("user_id") UUID userId) {
        return userSettingService.getUserSettingByUserId(userId);
    }

    @PostMapping("/user/{user_id}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("#userId == authentication.principal.user_id")
    public UserSettingResponseDTO createUserSetting(@PathVariable("user_id") UUID userId, @RequestBody @Validated UserSettingDTO body, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        }
        return userSettingService.createUserSetting(userId, body);
    }

    @PutMapping("/user/{user_id}")
    @PreAuthorize("#userId == authentication.principal.user_id")
    public UserSettingResponseDTO updateUserSetting(@PathVariable("user_id") UUID userId, @RequestBody @Validated UserSettingDTO body) {
        return userSettingService.updateUserSetting(userId, body);
    }

    @DeleteMapping("/{user_setting_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserSetting(@PathVariable("user_setting_id") UUID userSettingId, @AuthenticationPrincipal User currentUser) {
        userSettingService.deleteUserSetting(userSettingId, currentUser);
    }
}
