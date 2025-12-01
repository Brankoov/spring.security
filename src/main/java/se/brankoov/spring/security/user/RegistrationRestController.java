package se.brankoov.spring.security.user;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import se.brankoov.spring.security.user.authority.UserRole;
import se.brankoov.spring.security.user.dto.CustomUserCreationDTO;
import se.brankoov.spring.security.user.mapper.CustomUserMapper;

import java.util.Map;
import java.util.Set;

@RestController
public class RegistrationRestController {

    private final CustomUserRepository customUserRepository;
    private final CustomUserMapper customUserMapper;
    private final PasswordEncoder passwordEncoder;

    public RegistrationRestController(CustomUserRepository customUserRepository,
                                      CustomUserMapper customUserMapper,
                                      PasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.customUserMapper = customUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CustomUserCreationDTO dto) {

        // Enkel koll - vi kastar Exception istället för att returnera ResponseEntity manuellt
        // Detta låter GlobalExceptionHandler ta hand om felet på ett enhetligt sätt
        if (customUserRepository.existsByUsername(dto.username())) {
            throw new DataIntegrityViolationException("Username already taken");
        }

        CustomUser user = customUserMapper.toEntity(dto);

        // Hasha lösen
        user.setPassword(user.getPassword(), passwordEncoder);

        // Aktivera kontot
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        // Ge default-roll USER
        user.setUserRoles(Set.of(UserRole.USER));

        customUserRepository.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }
}