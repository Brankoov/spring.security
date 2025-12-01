package se.brankoov.spring.security.todo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import se.brankoov.spring.security.todo.dto.TodoRequestDTO;
import se.brankoov.spring.security.todo.dto.TodoResponseDTO;
import se.brankoov.spring.security.user.CustomUser;
import se.brankoov.spring.security.user.CustomUserRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/todos") // Gemensam start för alla endpoints
public class TodoRestController {

    private final TodoRepository todoRepository;
    private final CustomUserRepository userRepository;

    public TodoRestController(TodoRepository todoRepository, CustomUserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    // --- 1. HÄMTA MINA TODOS ---
    @GetMapping
    public List<TodoResponseDTO> getMyTodos(Authentication authentication) {
        // Hämta den inloggade användaren från databasen
        CustomUser user = getLoggedInUser(authentication);

        // Hämta bara DENNA användarens todos
        return todoRepository.findAllByUserOrderByCreatedDateDesc(user)
                .stream()
                .map(this::mapToDTO) // Gör om Entity till DTO
                .toList();
    }

    // --- 2. SKAPA EN NY TODO ---
    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody TodoRequestDTO dto, Authentication authentication) {
        CustomUser user = getLoggedInUser(authentication);

        Todo newTodo = new Todo(
                dto.title(),
                dto.description(),
                dto.dueDate(),
                user // Koppla todon till den inloggade användaren
        );

        todoRepository.save(newTodo);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(newTodo));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable UUID id, @RequestBody TodoRequestDTO dto, Authentication authentication) {
        CustomUser user = getLoggedInUser(authentication);

        Todo todoToUpdate = todoRepository.findById(id).orElse(null);

        if (todoToUpdate == null) {
            return ResponseEntity.notFound().build();
        }

        // SÄKERHET: Äger du den?
        if (!todoToUpdate.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You can only edit your own todos"));
        }

        // Uppdatera fälten
        todoToUpdate.setTitle(dto.title());
        todoToUpdate.setDescription(dto.description());
        todoToUpdate.setDueDate(dto.dueDate());
        todoToUpdate.setCompleted(dto.completed()); // Här sätter vi TRUE om den är klar

        todoRepository.save(todoToUpdate);

        return ResponseEntity.ok(mapToDTO(todoToUpdate));
    }

    // --- 3. TA BORT EN TODO ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable UUID id, Authentication authentication) {
        CustomUser user = getLoggedInUser(authentication);

        // Hämta todon och kolla om den finns
        Todo todoToDelete = todoRepository.findById(id).orElse(null);

        if (todoToDelete == null) {
            return ResponseEntity.notFound().build();
        }

        // SÄKERHETSKOLL: Äger du verkligen denna todo?
        if (!todoToDelete.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "You can only delete your own todos"));
        }

        todoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Todo deleted"));
    }

    // --- HJÄLPMETODER ---

    // Hjälpmetod för att hitta vem som är inloggad
    private CustomUser getLoggedInUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Hjälpmetod för att göra om Todo (Databas) -> DTO (JSON)
       private TodoResponseDTO mapToDTO(Todo todo) {
        return new TodoResponseDTO(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getCreatedDate(),
                todo.getDueDate(),
                todo.isCompleted() // Skicka med status
        );
    }
}