package com.bridgetrack.bridgetrack.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.EnrollmentStatus;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;
import com.bridgetrack.bridgetrack.repository.TermRepository;
import com.bridgetrack.bridgetrack.service.AdminAvailableSectionService;
import com.bridgetrack.bridgetrack.service.AdminEnrollmentValidationService;

@Controller
@RequestMapping("/admin")
public class AdminRegistrationController {

    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final TermRepository termRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final AdminEnrollmentValidationService adminEnrollmentValidationService;
    private final AdminAvailableSectionService adminAvailableSectionService;

    public AdminRegistrationController(
            StudentRepository studentRepository,
            SectionRepository sectionRepository,
            TermRepository termRepository,
            EnrollmentRepository enrollmentRepository,
            AdminEnrollmentValidationService adminEnrollmentValidationService,
            AdminAvailableSectionService adminAvailableSectionService
    ) {
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.termRepository = termRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.adminEnrollmentValidationService = adminEnrollmentValidationService;
        this.adminAvailableSectionService = adminAvailableSectionService;
    }

    @GetMapping("/registration")
    public String showRegistrationPage(
            @RequestParam(name = "studentId", required = false) Long studentId,
            @RequestParam(name = "termId", required = false) Long termId,
            @RequestParam(name = "modality", required = false, defaultValue = "ALL") String modality,
            @RequestParam(name = "query", required = false) String query,
            Model model
    ) {
        model.addAttribute("selectedStudentId", studentId);
        model.addAttribute("selectedTermId", termId);
        model.addAttribute("selectedModality", modality);
        model.addAttribute("query", query);

        if (studentId != null) {
            Student student = studentRepository.findById(studentId).orElse(null);
            model.addAttribute("selectedStudent", student);
        }

        model.addAttribute("terms", termRepository.findAll());

        // ✅ only show sections that are “available” to this student
        model.addAttribute("sections",
                adminAvailableSectionService.findAvailableSectionsForStudent(studentId, termId, modality, query));

        return "admin-registration";
    }

    @PostMapping("/registration/select")
    public String selectSectionForStudent(
            @RequestParam("studentId") Long studentId,
            @RequestParam("sectionId") Long sectionId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Optional<Enrollment> existing =
                    enrollmentRepository.findByStudentStudentIdAndSectionSectionId(studentId, sectionId);

            if (existing.isPresent() && existing.get().getStatus() != EnrollmentStatus.DROPPED) {
                redirectAttributes.addFlashAttribute("errorMessage", "Student is already enrolled in that section.");
                return "redirect:/admin/registration?studentId=" + studentId;
            }

            Student student = studentRepository.findById(studentId).orElseThrow(() ->
                    new RuntimeException("Student not found: " + studentId));

            Section candidate = sectionRepository.findById(sectionId).orElseThrow(() ->
                    new RuntimeException("Section not found: " + sectionId));

            // ✅ Safety net: even if it was shown, re-validate on submit (race/bypass proof)
            adminEnrollmentValidationService.validateCanEnroll(studentId, candidate);

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setSection(candidate);
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
            enrollment.setEnrolledAt(LocalDateTime.now());
            if (candidate.getTerm() != null) {
                enrollment.setPlannedTerm(candidate.getTerm().getTermName());
            }

            enrollmentRepository.save(enrollment);

            redirectAttributes.addFlashAttribute("successMessage", "Student enrolled successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/registration?studentId=" + studentId;
    }
}