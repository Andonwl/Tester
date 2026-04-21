package com.bridgetrack.bridgetrack.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.model.Instructor;
import com.bridgetrack.bridgetrack.model.User;
import com.bridgetrack.bridgetrack.repository.InstructorRepository;
import com.bridgetrack.bridgetrack.repository.UserRepository;

@Controller
@RequestMapping("/admin/instructors")
public class AdminInstructorController {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;

    public AdminInstructorController(
            InstructorRepository instructorRepository,
            UserRepository userRepository
    ) {
        this.instructorRepository = instructorRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showInstructorPage(Model model) {
        List<Instructor> instructors = instructorRepository.findAll();
        model.addAttribute("instructors", instructors);
        return "instructors";
    }
    
    @GetMapping("/add")
    public String showAddInstructorPage() {
        return "addInstructor";
    }

    @PostMapping("/create")
    public String createInstructor(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status
    ) {
        Instructor instructor = new Instructor();
        instructor.setFirstName(firstName);
        instructor.setLastName(lastName);
        instructor.setEmail(email);
        instructor.setPhone(phone);
        instructor.setRole(role != null && !role.isBlank() ? role : "INSTRUCTOR");
        instructor.setStatus(status != null && !status.isBlank() ? status : "ACTIVE");
        instructor.setCreatedAt(LocalDateTime.now());

        Instructor savedInstructor = instructorRepository.save(instructor);

        User user = new User();
        user.setUsername(firstName.toLowerCase() + "." + lastName.toLowerCase());
        user.setEmail(email);
        user.setPasswordHash("$2a$10$Dow1X3xR7rWz8h8s9KX0UeQ0zvYz7H3sK9Fz5Q7nQwQ5rZk9F3J6G");
        user.setEntityType("INSTRUCTOR");
        user.setEntityId(savedInstructor.getInstructorId());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "redirect:/admin/instructors";
    }

    @PostMapping("/update")
    public String updateInstructor(
            @RequestParam Long instructorId,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status
    ) {
        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);

        if (instructor != null) {
            instructor.setFirstName(firstName);
            instructor.setLastName(lastName);
            instructor.setEmail(email);
            instructor.setPhone(phone);
            instructor.setRole(role != null && !role.isBlank() ? role : "INSTRUCTOR");
            instructor.setStatus(status != null && !status.isBlank() ? status : "ACTIVE");

            instructorRepository.save(instructor);

            User user = userRepository.findByEntityTypeAndEntityId("INSTRUCTOR", instructorId).orElse(null);

            if (user != null) {
                user.setUsername(firstName.toLowerCase() + "." + lastName.toLowerCase());
                user.setEmail(email);
                user.setIsActive("ACTIVE".equalsIgnoreCase(instructor.getStatus()));
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        return "redirect:/admin/instructors";
    }

    @GetMapping("/delete/{id}")
    public String deleteInstructor(@PathVariable Long id) {
        User user = userRepository.findByEntityTypeAndEntityId("INSTRUCTOR", id).orElse(null);

        if (user != null) {
            userRepository.delete(user);
        }

        instructorRepository.deleteById(id);

        return "redirect:/admin/instructors";
    }
}