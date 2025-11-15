package se.brankoov.spring.security;
import se.brankoov.spring.security.user.CustomUser;
import se.brankoov.spring.security.user.CustomUserDetails;
import se.brankoov.spring.security.user.authority.UserRole;
import se.brankoov.spring.security.security.jwt.JwtUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		System.out.println(
				UserRole.GUEST.getRoleName()	// ROLE_GUEST
		);

		System.out.println(
				UserRole.USER.getUserPermissions() // PERMISSIONS
		);

		System.out.println(
				UserRole.ADMIN.getUserPermissions()
		);

		System.out.println(
				UserRole.GUEST.getUserAuthorities() + " \n " + // GUEST - AUTHORITY
						UserRole.USER.getUserAuthorities() + " \n " + // USER - AUTHORITY incl. perms.
						UserRole.ADMIN.getUserAuthorities()			 // ADMIN - AUTHORITY incl. perms.
		);

		CustomUser benny = new CustomUser(
				"",
				"",
				true,
				true,
				true,
				true,
				Set.of(UserRole.USER, UserRole.ADMIN)
		);
		/*
			CustomUserDetails customUserDetails = new CustomUserDetails(benny);
			System.out.println("getAuthorities: " + customUserDetails.getAuthorities());

			JwtUtils jwtUtils = new JwtUtils();

			// Generate the token
			String token = jwtUtils.generateJwtToken(benny);
			System.out.println("Generated JWT:\n" + token);

			// Extract the roles
			Set<UserRole> extractedRoles = jwtUtils.getRolesFromJwtToken(token);
			System.out.println("Extracted roles: " + extractedRoles);

		 */
	}

}