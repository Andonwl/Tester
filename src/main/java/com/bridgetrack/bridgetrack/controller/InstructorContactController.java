package com.bridgetrack.bridgetrack.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bridgetrack.bridgetrack.dto.InstructorRosterStudentDto;
import com.bridgetrack.bridgetrack.dto.InstructorSectionOptionDto;
import com.bridgetrack.bridgetrack.service.InstructorRosterService;

@Controller
public class InstructorContactController {

    private final InstructorRosterService instructorRosterService;

    public InstructorContactController(InstructorRosterService instructorRosterService) {
        this.instructorRosterService = instructorRosterService;
    }

    @GetMapping("/instructor/contact-info")
    public String showContactInfo(Authentication authentication, Model model) {
        String email = authentication.getName();
        Long instructorId = instructorRosterService.getLoggedInInstructorId(email);

        List<InstructorSectionOptionDto> sections =
                instructorRosterService.getInstructorSections(instructorId);

        Map<Long, InstructorRosterStudentDto> uniqueStudents = new LinkedHashMap<>();

        if (sections != null && !sections.isEmpty()) {
            for (InstructorSectionOptionDto section : sections) {
                List<InstructorRosterStudentDto> sectionRoster =
                        instructorRosterService.getRosterForSection(instructorId, section.getSectionId());

                for (InstructorRosterStudentDto student : sectionRoster) {
                    uniqueStudents.putIfAbsent(student.getStudentId(), student);
                }
            }
        }

        List<InstructorRosterStudentDto> roster = new ArrayList<>(uniqueStudents.values());
        roster.sort(
                Comparator.comparing(
                        InstructorRosterStudentDto::getLastName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                ).thenComparing(
                        InstructorRosterStudentDto::getFirstName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                )
        );

        model.addAttribute("roster", roster);
        return "contact-info";
    }
}