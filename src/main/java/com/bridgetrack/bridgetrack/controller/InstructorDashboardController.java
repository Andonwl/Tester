package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bridgetrack.bridgetrack.dto.InstructorRosterStudentDto;
import com.bridgetrack.bridgetrack.dto.InstructorSectionOptionDto;
import com.bridgetrack.bridgetrack.service.InstructorRosterService;

@Controller
public class InstructorDashboardController {

    private final InstructorRosterService instructorRosterService;

    public InstructorDashboardController(InstructorRosterService instructorRosterService) {
        this.instructorRosterService = instructorRosterService;
    }

    @GetMapping("/instructor/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Long instructorId = instructorRosterService.getLoggedInInstructorId(email);

        List<InstructorSectionOptionDto> sections =
                instructorRosterService.getInstructorSections(instructorId);

        model.addAttribute("sections", sections);

        if (sections != null && !sections.isEmpty()) {
            InstructorSectionOptionDto selectedSection = sections.get(0);
            List<InstructorRosterStudentDto> roster =
                    instructorRosterService.getRosterForSection(instructorId, selectedSection.getSectionId());

            model.addAttribute("selectedSection", selectedSection);
            model.addAttribute("roster", roster);
        } else {
            model.addAttribute("selectedSection", null);
            model.addAttribute("roster", null);
        }

        model.addAttribute("attendance", null);

        return "instructordashboard";
    }
}