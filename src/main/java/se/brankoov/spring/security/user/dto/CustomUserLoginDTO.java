package se.brankoov.spring.security.user.dto;

// TODO validation & Injection protection
public record CustomUserLoginDTO(
        String username,
        String password
) {
}