package se.brankoov.spring.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, UUID> {

    // Method will be called within UserDetailsService
    Optional<CustomUser> findUserByUsername(String username);

    boolean existsByUsername(String username);

    // Hitta alla användare där användarnamnet innehåller söktexten (IgnoreCase = stora/små bokstäver spelar ingen roll)
    List<CustomUser> findByUsernameContainingIgnoreCase(String username);
}