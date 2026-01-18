package com.library.library_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.library.library_system.model.User;
import com.library.library_system.service.UserService;
import com.springframework.boxes.oauth.starter.bean.SignUpRequest;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

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

    @GetMapping("/forgot_pass")
    public String forgotPassPage() {
        return "forgot_pass";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signup", new SignUpRequest());
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

    // Forgot Password - Send verification code
    @PostMapping("/forgot-password/send-code")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (!userService.findByEmail(email).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // For now, generate a simple code (in production, send via email)
        String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));
        
        // TODO: Store verification code temporarily (session, Redis, or database)
        // For now, we'll just return success and the frontend will accept any 6-digit code
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "Verification code sent to your email");
        return ResponseEntity.ok(response);
    }

    // Forgot Password - Verify code
    @PostMapping("/forgot-password/verify-code")
    @ResponseBody
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        // TODO: Verify the code against stored verification code
        // For now, accept any 6-digit code
        if (code == null || !code.matches("\\d{6}")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid verification code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "Code verified successfully");
        return ResponseEntity.ok(response);
    }

    // Forgot Password - Reset password
    @PostMapping("/forgot-password/reset")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        
        Optional<User> userOptional = userService.findByEmail(email);
        if (!userOptional.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Validate password length
        if (newPassword == null || newPassword.length() < 6) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Password must be at least 6 characters");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Update user password
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "Password reset successfully");
        return ResponseEntity.ok(response);
    }
}
