package se.brankoov.spring.security.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.brankoov.spring.security.todo.Todo;
import se.brankoov.spring.security.todo.TodoRepository;
import se.brankoov.spring.security.todo.dto.TodoResponseDTO;
import se.brankoov.spring.security.user.CustomUser;
import se.brankoov.spring.security.user.CustomUserRepository;
import se.brankoov.spring.security.user.dto.AdminUserDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin") // Prefix för hela klassen
public class AdminRestController {

    private final CustomUserRepository userRepository;
    private final TodoRepository todoRepository; // Nytt beroende

    public AdminRestController(CustomUserRepository userRepository, TodoRepository todoRepository) {
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public Map<String, Object> adminInfo(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities(),
                "message", "Only admins can see this"
        );
    }

    @GetMapping("/users")
    public List<AdminUserDTO> getAllUsers(@RequestParam(required = false) String search) {

        List<CustomUser> users;

        // Om sökterm finns och inte är tom -> Sök i DB
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCase(search);
        } else {
            // Annars hämta alla
            users = userRepository.findAll();
        }

        return users.stream()
                .map(user -> new AdminUserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.isEnabled(),
                        user.getRoles()
                ))
                .toList();
    }

    // --- NY ENDPOINT: Hämta todos för en specifik användare ---
    @GetMapping("/users/{userId}/todos")
    public ResponseEntity<?> getUserTodos(@PathVariable UUID userId) {
        // 1. Hämta användaren
        CustomUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Hämta todos för den användaren
        List<TodoResponseDTO> todos = todoRepository.findAllByUserOrderByCreatedDateDesc(user)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return ResponseEntity.ok(todos);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id, Authentication authentication) {
        CustomUser userToDelete = userRepository.findById(id).orElse(null);

        if (userToDelete == null) {
            return ResponseEntity.notFound().build();
        }

        String currentUsername = authentication.getName();
        if (userToDelete.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "You cannot delete your own account!"));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable UUID id, @RequestParam boolean enabled, Authentication authentication) {
        CustomUser userToUpdate = userRepository.findById(id).orElse(null);

        if (userToUpdate == null) {
            return ResponseEntity.notFound().build();
        }

        // SÄKERHET: Admin får inte avaktivera sig själv (risk för utlåsning)
        String currentUsername = authentication.getName();
        if (userToUpdate.getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "You cannot disable your own account!"));
        }

        // Uppdatera status
        userToUpdate.setEnabled(enabled);
        userRepository.save(userToUpdate);

        return ResponseEntity.ok(Map.of("message", "User status updated", "enabled", enabled));
    }

    // Hjälpmetod för att göra om Todo till DTO (samma som i TodoController)
    private TodoResponseDTO mapToDTO(Todo todo) {
        return new TodoResponseDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getCreatedDate(),
                todo.getDueDate(),
                todo.isCompleted()
        );
    }
}