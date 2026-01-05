package com.library.library_system;

import com.library.library_system.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
=======
import org.springframework.context.annotation.Bean;
>>>>>>> 7a9ba538864d4b472065c71cd4b7242122d23a6e

@SpringBootApplication(scanBasePackages = "com.library")
@EnableMongoRepositories(basePackages = "com.library.repository")
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