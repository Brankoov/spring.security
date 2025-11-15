package se.brankoov.spring.security.view;

import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.brankoov.spring.security.user.CustomUser;
import se.brankoov.spring.security.user.CustomUserRepository;
import se.brankoov.spring.security.user.authority.UserRole;

import javax.naming.Binding;

@Controller
public class CustomViewController {


    private final CustomUserRepository customUserRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomViewController(CustomUserRepository customUserRepository, PasswordEncoder passwordEncoder) {
        this.customUserRepository = customUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin")
    public String adminPage() {

        return "adminpage"; // Must Reflect .html document name
    }

    @GetMapping("/register")
    public String registerUser(Model model) {

        model.addAttribute("customUser", new CustomUser());

        return "registerpage";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid CustomUser customUser, BindingResult bindingResult, Model model
    ) {

        // TODO - Model model might not be necessery

        if (bindingResult.hasErrors()) {
            return "registerpage";
        }

        customUser.setPassword(
                passwordEncoder.encode(customUser.getPassword())
        );



        // TODO - Object mapper for shorter syntax

        customUser.setAccountNonExpired(true);
        customUser.setAccountNonLocked(true);
        customUser.setCredentialsNonExpired(true);
        customUser.setEnabled(true);



        System.out.println("Saving user...");
        customUserRepository.save(customUser);


        return "redirect:/login";
    }



}