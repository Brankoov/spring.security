package se.brankoov.spring.security.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.brankoov.spring.security.user.CustomUser;
import se.brankoov.spring.security.user.CustomUserRepository;
import se.brankoov.spring.security.user.dto.AdminUserDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class AdminRestController {

    private final CustomUserRepository userRepository;

    public AdminRestController(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public Map<String, Object> adminInfo(Authentication authentication) {
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

    // UPPDATERAD DELETE METOD
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id, Authentication authentication) {

        // 1. Hämta användaren vi vill ta bort
        CustomUser userToDelete = userRepository.findById(id).orElse(null);

        if (userToDelete == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. SÄKERHET: Kolla om den inloggade adminen försöker ta bort sig själv
        String currentUsername = authentication.getName();
        if (userToDelete.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "You cannot delete your own account!"));
        }

        // 3. Ta bort
        userRepository.deleteById(id);

        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}