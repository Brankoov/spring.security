package se.brankoov.spring.security.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserRestController {

    @GetMapping("/user")
    public Map<String, Object> currentUser(Authentication authentication) {
        // authentication kommer från JwtAuthenticationFilter + SecurityContext
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}
