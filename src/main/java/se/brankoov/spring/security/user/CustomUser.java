package se.brankoov.spring.security.user;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.brankoov.spring.security.user.authority.UserRole;
import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

/** Entity - Separation of Concerns
 * This SHOULD NOT implement UserDetails
 * Handle UserDetails separately as its own class for better SoC
 * Should however, reflect UserDetails Variables as best practice
 * */

@Table(name = "users")
@Entity
public class CustomUser {

    /** UUID
     * + Harder to accidentally expose
     * + Scales better in Global Apps (non-monolithic)
     * + Unique serial Key
     * - Harder to debug
     * - 16 bytes (2x larger than Long)
     * */
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    @Size(min = 2, max = 20, message = "Username must be between 2-20 characters")
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(
            regexp = "^" +
                    "(?=.*[a-z])" + // at least one lowercase letter
                    "(?=.*[A-Z])" + // at least one uppercase letter
                    "(?=.*[0-9])" + // at least one digit
                    "(?=.*[ @$!%*?&])" + // at least one special character
                    ".+$", // one or more characters, until end

                    message = "Password must contain at least one uppercase, one " +
                            "lowercase, one digit, and one special character"
    )
    @Size(max = 80, message = "Maximum length of password exceeded")
    private String password;
    private boolean isAccountNonExpired; //TODO- NOT NULL Bean validations
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    // TODO- NOTNULL for Enums
    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER) // Fetch Immediately
    @Enumerated(value = EnumType.STRING)
    private Set<UserRole> roles;

    // Constructors
    public CustomUser() {}

    public CustomUser(String username, String email, String password, boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired, boolean isEnabled, Set<UserRole> roles) {
        this.username = username;
        this.email = email; // <--- HÄR
        this.password = password;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setUserRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}