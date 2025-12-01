package se.brankoov.spring.security.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // 1. Hantera VALIDERINGSFEL (t.ex. @Pattern på lösenord)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        // Hämta det första felet och skicka till frontend
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.put("error", errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    // 2. Hantera FELAKTIGT LÖSENORD / ANVÄNDARE HITTAS EJ
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleAuthExceptions(Exception ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
    }

    // 3. Hantera GENERMELLA FEL (Catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        log.error("An unexpected error occurred", ex); // Loggar hela stacktrace för oss utvecklare

        // Skicka ett snällt meddelande till användaren
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Something went wrong. Please try again later."));
    }
    // 4. Hantera DATABAS-KONFLIKTER (t.ex. Username already exists)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());

        // Vi antar att det oftast handlar om username i detta projekt
        return ResponseEntity.status(HttpStatus.CONFLICT) // 409 Conflict
                .body(Map.of("error", "Username or data already exists. Please choose another one."));
    }
    // 5. Hantera INAKTIVT KONTO (Disabled)
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, String>> handleDisabledException(DisabledException ex) {
        log.warn("Login attempt on disabled account: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                .body(Map.of("error", "Your account is disabled. Please contact an Admin."));
    }
}