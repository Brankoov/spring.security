package se.brankoov.spring.security.admin;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.brankoov.spring.security.user.CustomUserRepository;
import se.brankoov.spring.security.user.dto.AdminUserDTO;

import java.util.List;
import java.util.Map;

@RestController
public class AdminRestController {

    private final CustomUserRepository userRepository;

    public AdminRestController(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public Map<String, Object> adminInfo(Authentication authentication) {
        // Hit kommer du bara om du har ROLE_ADMIN pga SecurityConfig
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities(),
                "message", "Only admins can see this"
        );
    }
    @GetMapping("/admin/users")
    public List<AdminUserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new AdminUserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.isEnabled(),
                        user.getRoles()
                ))
                .toList();
    }
}

