package com.bridgetrack.bridgetrack.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bridgetrack.bridgetrack.dto.StudentDashboardDto;
import com.bridgetrack.bridgetrack.service.StudentDashboardService;

@Controller
@RequestMapping("/student/dashboard")
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    public StudentDashboardController(StudentDashboardService studentDashboardService) {
        this.studentDashboardService = studentDashboardService;
    }

    @GetMapping
    public String showDashboard(Authentication authentication, Model model) {
        String studentEmail = authentication.getName();

        StudentDashboardDto dashboard = studentDashboardService.getDashboardData(studentEmail);

        model.addAttribute("upcomingTermName", dashboard.getUpcomingTermName());
        model.addAttribute("upcomingTermDateRange", dashboard.getUpcomingTermDateRange());
        model.addAttribute("upcomingClasses", dashboard.getUpcomingClasses());

        return "studentdashboard";
    }
}