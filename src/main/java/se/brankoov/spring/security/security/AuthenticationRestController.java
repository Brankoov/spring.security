package se.brankoov.spring.security.security;

import se.brankoov.spring.security.config.RabbitConfig;
import se.brankoov.spring.security.consumer.dto.EmailRequestDTO;
import se.brankoov.spring.security.security.jwt.JwtUtils;
import se.brankoov.spring.security.user.CustomUserDetails;
import se.brankoov.spring.security.user.dto.CustomUserLoginDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthenticationRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final AmqpTemplate amqpTemplate;

    @Autowired
    public AuthenticationRestController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, AmqpTemplate amqpTemplate) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.amqpTemplate = amqpTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @RequestBody CustomUserLoginDTO customUserLoginDTO,
            HttpServletResponse response
    ) {
        logger.debug("Attempting authentication for user: {}", customUserLoginDTO.username());

        // Step 1: Perform authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        customUserLoginDTO.username(),
                        customUserLoginDTO.password())
        );

        // 🧩 DEBUG: Print full Authentication result
        System.out.println("\n========= AUTHENTICATION RESULT =========");
        System.out.println("Class: " + authentication.getClass().getSimpleName());
        System.out.println("Authenticated: " + authentication.isAuthenticated());

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            System.out.println("  Username: " + userDetails.getUsername());
            System.out.println("  Authorities: " + userDetails.getAuthorities());
        }
        System.out.println("=========================================\n");

        // Step 2: Extract your custom principal
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // Step 3: Generate JWT using your domain model
        String token = jwtUtils.generateJwtToken(customUserDetails.getCustomUser());

        // Step 4: Set cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 1 hour
        response.addCookie(cookie);

        logger.info("Authentication successful for user: {}", customUserLoginDTO.username());

        // --- RABBITMQ EMAIL LOGIC (UPDATED) ---
        // Hämta den riktiga mailadressen från Entity-objektet
        String userEmail = customUserDetails.getCustomUser().getEmail();

        EmailRequestDTO emailRequest = new EmailRequestDTO(
                userEmail, // <--- SKICKAR TILL RIKTIG MAIL NU
                "New Login Detected",
                "Hi " + customUserLoginDTO.username() + "!\n\nWe detected a new login to your Todo App.\nIf this wasn't you, please contact admin."
        );

        amqpTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                emailRequest
        );

        // Step 5: Return token
        return ResponseEntity.ok(Map.of(
                "username", customUserLoginDTO.username(),
                "authorities", customUserDetails.getAuthorities(),
                "token", token
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        logger.info("User logged out (JWT cookie cleared)");

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Logged out"
        ));
    }
}