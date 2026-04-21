package com.bridgetrack.bridgetrack.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bridgetrack.bridgetrack.service.StudentScheduleService;

@Controller
@RequestMapping("/student/schedule")
public class MySchedulePageController {

    private final StudentScheduleService studentScheduleService;

    public MySchedulePageController(StudentScheduleService studentScheduleService) {
        this.studentScheduleService = studentScheduleService;
    }

    @GetMapping
    public String showMySchedulePage(Authentication authentication, Model model) {
        String studentEmail = authentication.getName();
        Map<String, Object> pageData = studentScheduleService.getSchedulePageData(studentEmail);
        model.addAllAttributes(pageData);
        return "my-schedule";
    }

    @PostMapping("/drop")
    public String dropCourse(Authentication authentication,
                             @RequestParam Long sectionId,
                             RedirectAttributes redirectAttributes) {
        String studentEmail = authentication.getName();

        try {
            // Drops Enrollment and syncs PlannedCourse.enrolled=false (handled in the service)
            studentScheduleService.dropSectionForStudent(studentEmail, sectionId);
            redirectAttributes.addFlashAttribute("successMessage", "Course dropped successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/student/schedule";
    }
}