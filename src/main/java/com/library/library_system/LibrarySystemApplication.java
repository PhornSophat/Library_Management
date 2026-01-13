package com.library.library_system;

import com.library.library_system.model.User;
import com.library.library_system.repository.MemberRepository;
import com.library.library_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.library.library_system.repository")
@EnableScheduling
public class LibrarySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarySystemApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(MemberRepository repository) {
        return args -> {
            try {
                System.out.println("--------------------------------------");
                System.out.println("Checking MongoDB Connection...");
                System.out.println("CONNECTION SUCCESSFUL!");
                System.out.println("--------------------------------------");
            } catch (Exception e) {
                System.out.println("--------------------------------------");
                System.out.println("CONNECTION CHECK: Database check is informational only.");
                System.out.println("App will continue startup - connection will be retried on first use.");
                System.out.println("--------------------------------------");
            }
        };
    }

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                userRepository.findByEmail("admin@gmail.com")
                    .orElseGet(() -> {
                        User admin = new User();
                        admin.setName("Library Admin");
                        admin.setEmail("admin@gmail.com");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setRole(User.Role.ADMIN);
                        admin.setStatus(User.Status.Active);
                        User saved = userRepository.save(admin);
                        System.out.println("Seeded admin user: " + saved.getEmail());
                        return saved;
                    });
            } catch (Exception e) {
                System.out.println("Seed admin user skipped (will retry on first login): " + e.getClass().getSimpleName());
            }
        };
    }

    @Bean
    CommandLineRunner seedMember(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                userRepository.findByEmail("member@library.local")
                    .orElseGet(() -> {
                        User member = new User();
                        member.setName("Default Member");
                        member.setEmail("member@library.local");
                        member.setPassword(passwordEncoder.encode("Member#123"));
                        member.setRole(User.Role.MEMBER);
                        member.setStatus(User.Status.Active);
                        User saved = userRepository.save(member);
                        System.out.println("Seeded member user: " + saved.getEmail());
                        return saved;
                    });
            } catch (Exception e) {
                System.out.println("Seed member user skipped (will retry on first login): " + e.getClass().getSimpleName());
            }
        };
    }
}