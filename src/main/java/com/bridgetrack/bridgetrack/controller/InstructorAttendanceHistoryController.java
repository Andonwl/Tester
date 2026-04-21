package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bridgetrack.bridgetrack.dto.AttendanceHistorySessionDto;
import com.bridgetrack.bridgetrack.service.InstructorAttendanceHistoryService;

@Controller
@RequestMapping("/instructor/attendance/history")
public class InstructorAttendanceHistoryController {

    private final InstructorAttendanceHistoryService instructorAttendanceHistoryService;

    public InstructorAttendanceHistoryController(
            InstructorAttendanceHistoryService instructorAttendanceHistoryService) {
        this.instructorAttendanceHistoryService = instructorAttendanceHistoryService;
    }

    @GetMapping
    public String showAttendanceHistory(Authentication authentication, Model model) {
        String instructorEmail = authentication.getName();

        List<AttendanceHistorySessionDto> sessions =
                instructorAttendanceHistoryService.getAttendanceHistoryForInstructor(instructorEmail);

        List<String> sectionOptions = sessions.stream()
                .map(AttendanceHistorySessionDto::getSectionName)
                .distinct()
                .toList();

        model.addAttribute("sessions", sessions);
        model.addAttribute("sectionOptions", sectionOptions);

        return "attendance-history";
    }
}