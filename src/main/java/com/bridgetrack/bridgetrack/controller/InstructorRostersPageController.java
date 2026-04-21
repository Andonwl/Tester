package com.bridgetrack.bridgetrack.controller;

import java.nio.charset.StandardCharsets;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.dto.InstructorRosterStudentDto;
import com.bridgetrack.bridgetrack.dto.InstructorSectionOptionDto;
import com.bridgetrack.bridgetrack.service.InstructorRosterService;
import com.bridgetrack.bridgetrack.service.RosterPdfService;

@Controller
@RequestMapping("/instructor/rosters")
public class InstructorRostersPageController {

    private final InstructorRosterService instructorRosterService;
    private final RosterPdfService rosterPdfService;

    public InstructorRostersPageController(
            InstructorRosterService instructorRosterService,
            RosterPdfService rosterPdfService) {

        this.instructorRosterService = instructorRosterService;
        this.rosterPdfService = rosterPdfService;
    }

    @GetMapping
    public String showRostersPage(@RequestParam(required = false) Long sectionId,
                                 Authentication authentication,
                                 Model model) {

        String email = authentication.getName();

        Long instructorId = instructorRosterService.getLoggedInInstructorId(email);

        List<InstructorSectionOptionDto> sectionOptions =
                instructorRosterService.getInstructorSections(instructorId);

        model.addAttribute("sectionOptions", sectionOptions);
        model.addAttribute("selectedSectionId", sectionId);

        if (sectionId != null) {
            InstructorSectionOptionDto selectedSection =
                    instructorRosterService.getSectionOption(instructorId, sectionId);

            List<InstructorRosterStudentDto> rosterStudents =
                    instructorRosterService.getRosterForSection(instructorId, sectionId);

            model.addAttribute("selectedSection", selectedSection);
            model.addAttribute("rosterStudents", rosterStudents);
            model.addAttribute("studentCount", rosterStudents.size());
        }

        return "rosters";
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportRosterPdf(
            @RequestParam Long sectionId,
            @RequestParam(defaultValue = "false") boolean view,
            Authentication authentication) {

        String email = authentication.getName();
        Long instructorId = instructorRosterService.getLoggedInInstructorId(email);

        InstructorSectionOptionDto selectedSection =
                instructorRosterService.getSectionOption(instructorId, sectionId);

        List<InstructorRosterStudentDto> students =
                instructorRosterService.getRosterForSection(instructorId, sectionId);

        byte[] pdf = rosterPdfService.generateRoster(selectedSection, students);

        String dispositionType = view ? "inline" : "attachment";

        String filename = "section-" + selectedSection.getSectionId() + "-roster.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        dispositionType + "; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private String escapeCsv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}