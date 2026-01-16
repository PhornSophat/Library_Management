package com.library.library_system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import com.library.library_system.repository.UserRepository;
import com.library.library_system.model.User;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler) throws Exception {
        http
            // Enable CSRF protection for production security
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // Ignore CSRF for API endpoints if needed
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/signup", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                .requestMatchers("/member/**").hasAuthority("MEMBER")
                .requestMatchers("/", "/members/**", "/books/**", "/admin/**", "/borrow", "/return", "/loans/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler(UserRepository userRepository) {
        return (request, response, authentication) -> {
            String email = authentication.getName();
            userRepository.findByEmail(email).ifPresentOrElse(user -> {
                try {
                    if (user.getRole() == User.Role.MEMBER) {
                        // Redirect members to their home portal
                        response.sendRedirect("/member/home");
                    } else {
                        // Redirect admins to the dashboard
                        response.sendRedirect("/");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                try {
                    response.sendRedirect("/");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }
}
