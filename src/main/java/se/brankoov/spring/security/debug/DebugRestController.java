package se.brankoov.spring.security.debug;
import se.brankoov.spring.security.user.CustomUser;
import se.brankoov.spring.security.user.CustomUserRepository;
import se.brankoov.spring.security.user.authority.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    // private final AppPasswordConfig appPasswordConfig; // ANTI-PATTERN (This is a config)
    private final PasswordEncoder passwordEncoder;
    private final CustomUserRepository customUserRepository;

    @Autowired
    public DebugRestController(PasswordEncoder passwordEncoder, CustomUserRepository customUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.customUserRepository = customUserRepository;
    }

    @GetMapping("/who-am-i")
    public String whoAmI() {

        // Using SecurityContextHolder (global access point)
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        // Using SecurityContext directly (once we have it)
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().toString();

        return "Hello, " + username + "! Your roles: " + authorities;
    }


    @GetMapping("/v2/who-am-i")
    public String whoAmI(Authentication authentication) {
        return "Hello, " + authentication.getName() +
                "! Your roles: " + authentication.getAuthorities();
    }

    @GetMapping("/auth-session")
    public ResponseEntity<String> debugAuthenticationSession(Authentication authentication) {
        System.out.println(authentication.getClass().getSimpleName());
        System.out.println(authentication.isAuthenticated());
        System.out.println(authentication);

        return ResponseEntity.ok().body("Check logs");
    }

    // Debugging HTTPSession Object from Tomcat & HttpServletRequest
    @GetMapping("/session-attributes")
    public ResponseEntity<String> debugSessionAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.ok("No session found.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Session ID: ").append(session.getId()).append("\n");
        sb.append("Attributes:\n");

        var names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Object value = session.getAttribute(name);
            sb.append(" • ").append(name)
                    .append(" = ").append(value)
                    .append("\n");
        }

        return ResponseEntity.ok(sb.toString());
    }


    @GetMapping("/create-debug-admin")
    public ResponseEntity<String> createDebugAdmin() {

        try {
            customUserRepository.save(
                    new CustomUser(
                            "Frida",
                            passwordEncoder.encode("321"),
                            true,
                            true,
                            true,
                            true,
                            Set.of(UserRole.ADMIN)
                    )
            );


            return ResponseEntity.status(HttpStatus.CREATED).body("User was SUCCESFULLY Created!");
        } catch (DataIntegrityViolationException exception) { // TODO - Username Already Exists
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists: " + exception.getLocalizedMessage()); // TODO - CONFLICT vs BAD REQUEST?
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body("Something went wrong..." + exception.getLocalizedMessage());
        } finally {
            System.out.println("Creating debug user function - ENDED");
        }

    }

    @GetMapping
    public ResponseEntity<String> testBcryptEncoding(
            @RequestParam(value = "message") String message         // ?message=""
    ) {

        String obfuscatedMessage = passwordEncoder.encode(message); // Turn Message into BCrypt Hash

        return ResponseEntity.ok().body(
                "Message was: " + message + " and was hashed into " + obfuscatedMessage
                // Same passwords, yield different results (Safety through Salting)
                // $2a$10$EIQjuLSX6iXknFlK0j/bSu6BlFcfJCvXw4N85JpnW1OXmmoIMyuJ. // 72 Length
                // $2a = Version
                // $10 = Strength (Iteration)
                // Salt = EIQjuLSX6iXknFlK (16 Length)
                // 0j/bSu6BlFcfJCvXw4N85JpnW1OXmmoIMyuJ. (37 Length???)
        );
    }

}