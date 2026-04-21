package com.bridgetrack.bridgetrack.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.dto.StudentRegistrationRequest;
import com.bridgetrack.bridgetrack.dto.StudentRegistrationResult;
import com.bridgetrack.bridgetrack.dto.StudentUpdateRequest;
import com.bridgetrack.bridgetrack.exception.DuplicateStudentException;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
   

    public Student updateStudent(Long id, StudentUpdateRequest request) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setPersonalEmail(request.getPersonalEmail());
        student.setPhone(request.getPhone());

        if (request.getBirthdate() != null && !request.getBirthdate().isBlank()) {
            student.setBirthDate(java.time.LocalDate.parse(request.getBirthdate()));
        } else {
            student.setBirthDate(null);
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            student.setStatus(request.getStatus());
        } else {
            student.setStatus(null);
        }

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public StudentRegistrationResult registerStudent(StudentRegistrationRequest request) {

        if (request.getFirstName() == null || request.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }

        if (request.getLastName() == null || request.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        if (request.getPersonalEmail() == null || request.getPersonalEmail().isBlank()) {
            throw new IllegalArgumentException("Personal email is required.");
        }

        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone is required.");
        }

        if (studentRepository.existsByPersonalEmailIgnoreCase(request.getPersonalEmail().trim())) {
            throw new DuplicateStudentException("A student account with that personal email already exists.");
        }

        String studentEmail = generateUniqueStudentEmail(request.getFirstName(), request.getLastName());
        String rawPassword = generateTemporaryPassword(
                request.getFirstName(),
                request.getLastName()
        );

        Student student = new Student();
        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setPersonalEmail(request.getPersonalEmail().trim());
        student.setPhone(request.getPhone().trim());
        student.setBirthDate(request.getBirthDate());
        student.setStudentEmail(studentEmail);
        student.setRegisteredAt(LocalDate.now());
        student.setStatus("ACTIVE");
        student.setPossibleDuplicate(false);
        student.setPasswordHash(passwordEncoder.encode(rawPassword));

        Student saved = studentRepository.save(student);

        return new StudentRegistrationResult(
                saved.getStudentId(),
                studentEmail,
                rawPassword
        );
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    private String generateUniqueStudentEmail(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
        String last = lastName == null ? "" : lastName.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");

        if (first.isBlank()) {
            first = "student";
        }

        if (last.isBlank()) {
            last = "user";
        }

        String base = first + "." + last;
        String email = base + "@bridgetrack.edu";

        int counter = 1;
        while (studentRepository.existsByStudentEmailIgnoreCase(email)) {
            email = base + counter + "@bridgetrack.edu";
            counter++;
        }

        return email;
    }

    private String generateTemporaryPassword(String firstName, String lastName) {
        String first = firstName == null ? "" : firstName.trim();
        String last = lastName == null ? "" : lastName.trim();

        String firstPart = first.length() >= 3
                ? first.substring(0, 3)
                : first;

        String lastPart = last.length() >= 3
                ? last.substring(0, 3)
                : last;

        return capitalize(lastPart) + capitalize(firstPart) + "$";
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        return input.substring(0, 1).toUpperCase() +
               input.substring(1).toLowerCase();
    }
}