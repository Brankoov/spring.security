package se.brankoov.spring.security.debug;

import se.brankoov.spring.security.user.CustomUser;
import se.brankoov.spring.security.user.CustomUserRepository;
import se.brankoov.spring.security.user.authority.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/debug")
public class DebugRestController {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserRepository customUserRepository;

    @Autowired
    public DebugRestController(PasswordEncoder passwordEncoder, CustomUserRepository customUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.customUserRepository = customUserRepository;
    }

    @GetMapping("/who-am-i")
    public String whoAmI() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return "Hello, " + authentication.getName() + "! Your roles: " + authentication.getAuthorities().toString();
    }

    @GetMapping("/create-debug-admin")
    public ResponseEntity<String> createDebugAdmin() {
        // Ingen try-catch behövs! GlobalExceptionHandler fångar DataIntegrityViolationException
        // om användaren redan finns.

        customUserRepository.save(
                new CustomUser(
                        "Admin",
                        "admin@company.com",
                        passwordEncoder.encode("Admin123!"),
                        true,
                        true,
                        true,
                        true,
                        Set.of(UserRole.ADMIN)
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body("User was SUCCESFULLY Created!");
    }

    @GetMapping
    public ResponseEntity<String> testBcryptEncoding(@RequestParam(value = "message") String message) {
        String obfuscatedMessage = passwordEncoder.encode(message);
        return ResponseEntity.ok().body("Message was: " + message + " and was hashed into " + obfuscatedMessage);
    }
}