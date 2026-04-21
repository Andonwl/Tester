package com.bridgetrack.bridgetrack.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.StudentRepository;
import com.bridgetrack.bridgetrack.config.StudentPasswordGenerator;

@Configuration
public class PasswordHashInitializer {

    @Bean
    public CommandLineRunner initializePasswordHashes(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                var studentsWithoutPassword = studentRepository.findAll()
                        .stream()
                        .filter(s -> s.getPasswordHash() == null || s.getPasswordHash().isBlank())
                        .toList();

                if (studentsWithoutPassword.isEmpty()) {
                    System.out.println("✓ All students have password hashes. No initialization needed.");
                    return;
                }

                System.out.println("Initializing password hashes for " + studentsWithoutPassword.size() + " students...");

                for (Student student : studentsWithoutPassword) {
                    String tempPassword = StudentPasswordGenerator.fromEmail(student.getStudentEmail());
                    student.setPasswordHash(passwordEncoder.encode(tempPassword));
                    studentRepository.save(student);
                    System.out.println("  ✓ Initialized: " + student.getStudentEmail() + " -> Password: " + tempPassword);
                }

                System.out.println("Password initialization completed for " + studentsWithoutPassword.size() + " students");
            } catch (Exception e) {
                System.err.println("Error initializing password hashes: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}