package se.brankoov.spring.security.user.mapper;


import se.brankoov.spring.security.user.CustomUser;
import se.brankoov.spring.security.user.dto.CustomUserCreationDTO;
import se.brankoov.spring.security.user.dto.CustomUserResponseDTO;
import org.springframework.stereotype.Component;

/** CustomUserMapper:
 *   Converts CustomUser to Entity.
 *   Converts Entity to UsernameDTO
 * */

@Component
public class CustomUserMapper {

    public CustomUser toEntity(CustomUserCreationDTO customUserCreationDTO) {

        return new CustomUser(
                customUserCreationDTO.username(),
                customUserCreationDTO.email(),
                customUserCreationDTO.password(),
                customUserCreationDTO.isAccountNonExpired(),
                customUserCreationDTO.isAccountNonLocked(),
                customUserCreationDTO.isCredentialsNonExpired(),
                customUserCreationDTO.isEnabled(),
                customUserCreationDTO.roles()
        );
    }

    public CustomUserResponseDTO toUsernameDTO(CustomUser customUser) {

        return new CustomUserResponseDTO(customUser.getUsername());
    }

}