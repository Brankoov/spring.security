package se.brankoov.spring.security.user.dto;

import se.brankoov.spring.security.user.authority.UserRole;
import java.util.Set;
import java.util.UUID;

public record AdminUserDTO(
        UUID id,
        String username,
        boolean isEnabled,
        Set<UserRole> roles
) {}