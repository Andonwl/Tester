package com.bridgetrack.bridgetrack.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Controller
public class StudentProfileController {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentProfileController(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/student/profile")
    public String profile(Model model, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;

        if (username == null) {
            model.addAttribute("errorMessage", "You are not logged in.");
            return "student-profile";
        }

        Student s = studentRepository.findByStudentEmailIgnoreCase(username)
                .or(() -> studentRepository.findByPersonalEmailIgnoreCase(username))
                .orElse(null);

        if (s == null) {
            model.addAttribute("errorMessage", "Student record not found for: " + username);
            return "student-profile";
        }

        loadProfileModel(model, s);
        return "student-profile";
    }

    @PostMapping("/student/profile/password")
    public String updatePassword(
            Model model,
            Authentication authentication,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    ) {
        String username = authentication != null ? authentication.getName() : null;

        if (username == null) {
            model.addAttribute("errorMessage", "You are not logged in.");
            return "student-profile";
        }

        Student s = studentRepository.findByStudentEmailIgnoreCase(username)
                .or(() -> studentRepository.findByPersonalEmailIgnoreCase(username))
                .orElse(null);

        if (s == null) {
            model.addAttribute("errorMessage", "Student record not found for: " + username);
            return "student-profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "New password and confirmation do not match.");
            loadProfileModel(model, s);
            return "student-profile";
        }

        if (s.getPasswordHash() == null || !passwordEncoder.matches(currentPassword, s.getPasswordHash())) {
            model.addAttribute("errorMessage", "Current password is incorrect.");
            loadProfileModel(model, s);
            return "student-profile";
        }
        
        s.setPasswordHash(passwordEncoder.encode(newPassword));
        studentRepository.save(s);

        model.addAttribute("successMessage", "Password updated successfully.");
        loadProfileModel(model, s);
        return "student-profile";
    }

    private void loadProfileModel(Model model, Student s) {
        model.addAttribute("studentId", s.getStudentId());
        model.addAttribute("firstName", s.getFirstName());
        model.addAttribute("lastName", s.getLastName());
        model.addAttribute("email", s.getStudentEmail());
        model.addAttribute("personalEmail", s.getPersonalEmail());
        model.addAttribute("status", s.getStatus());
    }
}