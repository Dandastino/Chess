package dandastino.chess.userSettings;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user_settings")
public class UserSettingsController {

    private final UserSettingService userSettingService;

    public UserSettingsController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }
}
