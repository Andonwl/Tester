package com.bridgetrack.bridgetrack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bridgetrack.bridgetrack.dto.AdminAttendanceReportsPageData;
import com.bridgetrack.bridgetrack.service.AdminAttendanceReportsService;
import com.bridgetrack.bridgetrack.service.AttendanceReportPdfService;
import com.bridgetrack.bridgetrack.dto.AdminAttendanceSelectedSessionDto;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/admin/attendance-reports")
public class AdminAttendanceReportsController {

    private final AdminAttendanceReportsService adminAttendanceReportsService;
    private final AttendanceReportPdfService attendanceReportPdfService;

    public AdminAttendanceReportsController(AdminAttendanceReportsService adminAttendanceReportsService,
                                            AttendanceReportPdfService attendanceReportPdfService) {
        this.adminAttendanceReportsService = adminAttendanceReportsService;
        this.attendanceReportPdfService = attendanceReportPdfService;
    }

    @GetMapping
    public String showReportsPage(@RequestParam(required = false) Long instructorId,
                                  @RequestParam(required = false) Long courseId,
                                  @RequestParam(required = false) Long sectionId,
                                  @RequestParam(required = false) String sessionDate,
                                  @RequestParam(required = false) Long attendanceSessionId,
                                  Model model) {

        AdminAttendanceReportsPageData pageData =
                adminAttendanceReportsService.loadReportsPage(
                        instructorId,
                        courseId,
                        sectionId,
                        sessionDate,
                        attendanceSessionId
                );

        model.addAttribute("instructorOptions", pageData.getInstructorOptions());
        model.addAttribute("courseOptions", pageData.getCourseOptions());
        model.addAttribute("sectionOptions", pageData.getSectionOptions());
        model.addAttribute("attendanceSessions", pageData.getAttendanceSessions());

        model.addAttribute("selectedInstructorId", pageData.getSelectedInstructorId());
        model.addAttribute("selectedCourseId", pageData.getSelectedCourseId());
        model.addAttribute("selectedSectionId", pageData.getSelectedSectionId());
        model.addAttribute("selectedAttendanceSessionId", pageData.getSelectedAttendanceSessionId());
        model.addAttribute("selectedSessionDate", pageData.getSelectedSessionDate());

        model.addAttribute("selectedInstructorName", pageData.getSelectedInstructorName());
        model.addAttribute("selectedCourseCode", pageData.getSelectedCourseCode());
        model.addAttribute("selectedSectionLabel", pageData.getSelectedSectionLabel());
        model.addAttribute("sessionCount", pageData.getSessionCount());
        model.addAttribute("selectedReportSession", pageData.getSelectedReportSession());

        return "attendance-reports";
    }

    @GetMapping("/session")
    public String sessionDetail(@RequestParam Long attendanceSessionId,
                                Model model) {

        AdminAttendanceSelectedSessionDto session =
                adminAttendanceReportsService.loadSelectedSession(attendanceSessionId);

        model.addAttribute("selectedReportSession", session);
        return "attendance-session-detail";
    }

    @GetMapping("/view")
    public String previewSession(@RequestParam Long attendanceSessionId,
                                 RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Preview endpoint not wired yet. Selected session ID: " + attendanceSessionId
        );

        return "redirect:/admin/attendance-reports?attendanceSessionId=" + attendanceSessionId;
    }

    @GetMapping("/export")
    public void exportSession(@RequestParam Long attendanceSessionId,
                              HttpServletResponse response) throws IOException {

        AdminAttendanceSelectedSessionDto session =
                adminAttendanceReportsService.loadSelectedSession(attendanceSessionId);

        byte[] pdf = attendanceReportPdfService.generateSessionReport(session);

        String filename = "attendance-session-" + attendanceSessionId + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");        
        response.setContentLength(pdf.length);
        response.getOutputStream().write(pdf);
    }

    private String value(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}