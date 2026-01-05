package com.library.library_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.library.library_system.model.User;
import com.library.library_system.service.UserService;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // Authentication is handled by Spring Security formLogin at /login
    // The role-based redirection is implemented in SecurityConfig's
    // AuthenticationSuccessHandler bean.

    @PostMapping("/signup")
    public String handleSignup(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        
        // Validate email doesn't exist
        if (userService.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already exists. Please login instead.");
            return "redirect:/signup";
        }

        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/signup";
        }

        // Validate password length
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters.");
            return "redirect:/signup";
        }

        // Create new user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.MEMBER);
        user.setStatus(User.Status.Active);
        
        userService.createUser(user);

        redirectAttributes.addFlashAttribute("success", "Account created successfully! Please login.");
        return "redirect:/login";
    }
}
