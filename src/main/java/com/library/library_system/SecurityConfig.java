package com.library.library_system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // ✅ Important: Disable CSRF so your POST form works
            .authorizeHttpRequests(auth -> auth
                // Permit the update route explicitly
                .requestMatchers("/admin/update-credentials").permitAll() 
                .requestMatchers("/dashboard/**").permitAll() // Temporarily permit all for testing
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                // .loginPage("/login") ❌ Comment this out until you actually create a login.html
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }
    
}
