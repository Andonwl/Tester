package com.bridgetrack.bridgetrack.controller;

import com.bridgetrack.bridgetrack.dto.StudentRegistrationRequest;
import com.bridgetrack.bridgetrack.dto.StudentRegistrationResult;
import com.bridgetrack.bridgetrack.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final StudentService studentService;

    public RegistrationController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        if (!model.containsAttribute("studentRegistration")) {
            model.addAttribute("studentRegistration", new StudentRegistrationRequest());
        }
        return "registration";
    }

    @PostMapping("/register")
    public String registerStudent(
            @ModelAttribute("studentRegistration") StudentRegistrationRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            StudentRegistrationResult result = studentService.registerStudent(request);

            model.addAttribute("studentEmail", result.getStudentEmail());
            model.addAttribute("rawPassword", result.getRawPassword());

            return "registration-success";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("studentRegistration", request);
            return "redirect:/registration";
        }
    }
}