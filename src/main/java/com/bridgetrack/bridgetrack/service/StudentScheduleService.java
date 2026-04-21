package com.bridgetrack.bridgetrack.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgetrack.bridgetrack.dto.StudentScheduleRowDto;
import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.PlannedCourse;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.PlannedCourseRepository;

@Service
public class StudentScheduleService {

    private final EnrollmentRepository enrollmentRepository;
    private final PlannedCourseRepository plannedCourseRepository;

    public StudentScheduleService(EnrollmentRepository enrollmentRepository,
                                  PlannedCourseRepository plannedCourseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.plannedCourseRepository = plannedCourseRepository;
    }

    public Map<String, Object> getSchedulePageData(String studentEmail) {
        List<Enrollment> enrollments = enrollmentRepository.findScheduleByStudentStudentEmail(studentEmail);

        Map<String, List<StudentScheduleRowDto>> scheduleByTerm = new LinkedHashMap<>();
        List<StudentScheduleRowDto> rows = new ArrayList<>();

        String studentName = "Student";
        String currentTermName = "N/A";
        String programName = "Not Available";

        if (!enrollments.isEmpty() && enrollments.get(0).getStudent() != null) {
            String firstName = valueOrBlank(enrollments.get(0).getStudent().getFirstName());
            String lastName = valueOrBlank(enrollments.get(0).getStudent().getLastName());
            String fullName = (firstName + " " + lastName).trim();
            if (!fullName.isBlank()) {
                studentName = fullName;
            }
        }

        for (Enrollment enrollment : enrollments) {
            Section section = enrollment.getSection();
            if (section == null || section.getCourse() == null) {
                continue;
            }

            String termName = resolveDisplayTerm(enrollment, section);

            if ("N/A".equals(currentTermName)) {
                currentTermName = termName;
            }

            if (section.getCourse().getProgram() != null
                    && section.getCourse().getProgram().getProgramName() != null
                    && !section.getCourse().getProgram().getProgramName().isBlank()) {
                programName = section.getCourse().getProgram().getProgramName();
            }

            StudentScheduleRowDto row = new StudentScheduleRowDto();
            row.setTermName(termName);
            row.setCourseCode(safe(section.getCourse().getCourseCode()));
            row.setCourseTitle(safe(section.getCourse().getCourseName()));
            row.setSectionId(section.getSectionId());
            row.setMeetingDays(formatMeetingDays(section.getMeetingDays()));
            row.setStartTime(formatTime(section.getStartTime()));
            row.setEndTime(formatTime(section.getEndTime()));
            row.setModality(safe(section.getModality()));
            row.setLocation(safe(section.getLocation()));
            row.setInstructorName("TBA");
            row.setStatus(enrollment.getStatus() != null ? enrollment.getStatus().toString() : "ENROLLED");

            rows.add(row);
            scheduleByTerm.computeIfAbsent(termName, key -> new ArrayList<>()).add(row);
        }

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("studentName", studentName);
        pageData.put("programName", programName);
        pageData.put("currentTermName", currentTermName);
        pageData.put("totalSections", rows.size());
        pageData.put("scheduleByTerm", scheduleByTerm);

        return pageData;
    }

    @Transactional
    public void dropSectionForStudent(String studentEmail, Long sectionId) {
        if (sectionId == null) {
            throw new RuntimeException("Section not found.");
        }

        // Find enrollment first (we need course + plannedTerm to update PlannedCourse accurately)
        Enrollment enrollment = enrollmentRepository
                .findByStudentStudentEmailAndSectionSectionId(studentEmail, sectionId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found for this section."));

        Long studentId = enrollment.getStudent() != null ? enrollment.getStudent().getStudentId() : null;
        Long courseId = (enrollment.getSection() != null && enrollment.getSection().getCourse() != null)
                ? enrollment.getSection().getCourse().getCourseId()
                : null;

        if (studentId == null || courseId == null) {
            throw new RuntimeException("Could not resolve student/course for dropped enrollment.");
        }

        // Parse "Spring 2026 - Term 1" into ("Spring 2026", 1)
        String plannedTerm = valueOrBlank(enrollment.getPlannedTerm());
        Optional<TermParts> termPartsOpt = parsePlannedTerm(plannedTerm);

        boolean updatedPlanner = false;

        if (termPartsOpt.isPresent()) {
            TermParts parts = termPartsOpt.get();

            Optional<PlannedCourse> pcOpt =
                    plannedCourseRepository.findByStudentStudentIdAndCourseCourseIdAndSemesterLabelAndTermNumber(
                            studentId, courseId, parts.semesterLabel(), parts.termNumber()
                    );

            if (pcOpt.isPresent()) {
                PlannedCourse pc = pcOpt.get();
                if (pc.isEnrolled()) {
                    pc.setEnrolled(false);
                    plannedCourseRepository.save(pc);
                }
                updatedPlanner = true;
            }
        }

        // Fallback: if plannedTerm missing or didn't match, still try to flip *an* enrolled planned course
        // (prevents the counter bug from persisting due to bad/missing plannedTerm data)
        if (!updatedPlanner) {
            plannedCourseRepository
                    .findFirstByStudentStudentIdAndCourseCourseIdAndEnrolledTrueOrderBySemesterLabelDescTermNumberDesc(studentId, courseId)
                    .ifPresent(pc -> {
                        pc.setEnrolled(false);
                        plannedCourseRepository.save(pc);
                    });
        }

        // Finally delete the enrollment row
        enrollmentRepository.delete(enrollment);
    }

    private Optional<TermParts> parsePlannedTerm(String plannedTerm) {
        if (plannedTerm == null || plannedTerm.isBlank()) {
            return Optional.empty();
        }

        // Expected format: "<semesterLabel> - Term <N>"
        // Example: "Spring 2026 - Term 1"
        String[] parts = plannedTerm.split("\\s+-\\s+Term\\s+");
        if (parts.length != 2) {
            return Optional.empty();
        }

        String semesterLabel = valueOrBlank(parts[0]);
        String termNumStr = valueOrBlank(parts[1]);

        if (semesterLabel.isBlank() || termNumStr.isBlank()) {
            return Optional.empty();
        }

        try {
            int termNumber = Integer.parseInt(termNumStr);
            return Optional.of(new TermParts(semesterLabel, termNumber));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private record TermParts(String semesterLabel, Integer termNumber) {}

    private String resolveDisplayTerm(Enrollment enrollment, Section section) {
        String plannedTerm = valueOrBlank(enrollment.getPlannedTerm());
        if (!plannedTerm.isBlank()) {
            return plannedTerm;
        }

        if (section.getTerm() != null) {
            String sectionTermName = valueOrBlank(section.getTerm().getTermName());
            if (!sectionTermName.isBlank()) {
                return sectionTermName;
            }
        }

        return "Term";
    }

    private String formatTime(LocalTime time) {
        if (time == null) {
            return "TBA";
        }
        return time.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    private String formatMeetingDays(String meetingDays) {
        String value = valueOrBlank(meetingDays);
        if (value.isBlank()) {
            return "TBA";
        }

        return switch (value.toUpperCase()) {
            case "MW" -> "Mon/Wed";
            case "TR" -> "Tue/Thu";
            case "MTWTH" -> "Mon/Tue/Wed/Thu";
            case "M" -> "Mon";
            case "T" -> "Tue";
            case "W" -> "Wed";
            case "R" -> "Thu";
            case "F" -> "Fri";
            case "S" -> "Sat";
            case "SU" -> "Sun";
            default -> value;
        };
    }

    private String safe(String value) {
        String cleaned = valueOrBlank(value);
        return cleaned.isBlank() ? "TBA" : cleaned;
    }

    private String valueOrBlank(String value) {
        return value == null ? "" : value.trim();
    }
}