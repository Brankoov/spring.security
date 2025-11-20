package se.brankoov.spring.security.admin;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminRestController {

    @GetMapping("/admin")
    public Map<String, Object> adminInfo(Authentication authentication) {
        // Hit kommer du bara om du har ROLE_ADMIN pga SecurityConfig
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities(),
                "message", "Only admins can see this"
        );
    }
}
