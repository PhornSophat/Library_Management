package com.library.library_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library_system.model.User;
import com.library.library_system.service.UserService;
import com.library.library_system.dto.SignupRequest;

@Controller
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signup", new SignupRequest());
        return "signup";
    }

    // Authentication is handled by Spring Security formLogin at /login
    // The role-based redirection is implemented in SecurityConfig's
    // AuthenticationSuccessHandler bean.

    @PostMapping("/signup")
    public String handleSignup(@Valid @ModelAttribute("signup") SignupRequest signup,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the highlighted errors.");
            return "redirect:/signup";
        }

        // Validate email doesn't exist
        if (userService.findByEmail(signup.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already exists. Please login instead.");
            return "redirect:/signup";
        }

        // Validate passwords match
        if (!signup.passwordsMatch()) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/signup";
        }

        // Create new user
        User user = new User();
        user.setName(signup.getName());
        user.setEmail(signup.getEmail());
        // Leave raw password; UserService will encode exactly once to avoid double-hashing
        user.setPassword(signup.getPassword());
        user.setRole(User.Role.MEMBER);
        user.setStatus(User.Status.Active);
        
        userService.createUser(user);

        redirectAttributes.addFlashAttribute("success", "Account created successfully! Please login.");
        return "redirect:/login";
    }
}
