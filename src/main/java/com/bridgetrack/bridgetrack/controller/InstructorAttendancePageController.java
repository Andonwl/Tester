package com.bridgetrack.bridgetrack.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bridgetrack.bridgetrack.dto.AttendancePageData;
import com.bridgetrack.bridgetrack.dto.AttendanceSaveRequest;
import com.bridgetrack.bridgetrack.model.AttendanceStatus;
import com.bridgetrack.bridgetrack.service.InstructorAttendanceService;

@Controller
@RequestMapping("/instructor/attendance")
public class InstructorAttendancePageController {

    private final InstructorAttendanceService instructorAttendanceService;

    public InstructorAttendancePageController(InstructorAttendanceService instructorAttendanceService) {
        this.instructorAttendanceService = instructorAttendanceService;
    }

    @GetMapping
    public String showAttendancePage(@RequestParam(required = false) Long sectionId,
                                     @RequestParam(required = false) LocalDate sessionDate,
                                     Authentication authentication,
                                     Model model) {

        String instructorEmail = authentication.getName();

        AttendancePageData pageData = instructorAttendanceService.loadAttendancePage(
                instructorEmail,
                sectionId,
                sessionDate
        );

        model.addAttribute("sectionOptions", pageData.getSectionOptions());
        model.addAttribute("selectedSection", pageData.getSelectedSection());
        model.addAttribute("selectedSectionId", pageData.getSelectedSectionId());
        model.addAttribute("sessionDate", pageData.getSessionDate());
        model.addAttribute("rosterStudents", pageData.getRosterStudents());
        model.addAttribute("attendanceMap", pageData.getAttendanceMap());
        model.addAttribute("isPastSession", pageData.isPastSession());

        return "record-attendance";
    }

    @PostMapping("/save")
    public String saveAttendance(@RequestParam Long sectionId,
                                 @RequestParam LocalDate sessionDate,
                                 @RequestParam Map<String, String> params,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        String instructorEmail = authentication.getName();

        try {
            AttendanceSaveRequest request = new AttendanceSaveRequest();
            request.setSectionId(sectionId);
            request.setSessionDate(sessionDate);

            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();

                if (!key.startsWith("status_")) {
                    continue;
                }

                String studentIdPart = key.substring("status_".length());
                Long studentId = Long.valueOf(studentIdPart);
                AttendanceStatus status = AttendanceStatus.valueOf(entry.getValue().trim().toUpperCase());

                request.getStudentStatuses().put(studentId, status);
            }

            instructorAttendanceService.saveAttendance(instructorEmail, request);

            redirectAttributes.addFlashAttribute("successMessage", "Attendance saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "Unable to save attendance.");
        }

        return "redirect:/instructor/attendance?sectionId=" + sectionId + "&sessionDate=" + sessionDate;
    }
}