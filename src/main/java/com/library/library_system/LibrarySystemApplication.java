package com.library.library_system;

import com.library.library_system.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
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
                long count = repository.count();
                System.out.println("CONNECTION SUCCESSFUL!");
                System.out.println("Number of members in database: " + count);
                System.out.println("--------------------------------------");
            } catch (Exception e) {
                System.err.println("--------------------------------------");
                System.err.println("CONNECTION FAILED: " + e.getMessage());
                System.err.println("--------------------------------------");
            }
        };
    }
}