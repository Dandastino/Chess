package dandastino.chess.utility;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary getCloudinary(@Value("${cloudinary.name}") String cloudName,
                                    @Value("${cloudinary.apikey}") String cloudApiKe,
                                    @Value("${cloudinary.secret}") String cloudApiSecret){
        Map<String, String> configuration = new HashMap<>();
        configuration.put("cloud_name", cloudName);
        configuration.put("api_key", cloudApiKe);
        configuration.put("api_secret", cloudApiSecret);
        return new Cloudinary(configuration);
    }
}
