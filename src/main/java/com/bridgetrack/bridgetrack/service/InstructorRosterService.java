package com.bridgetrack.bridgetrack.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.dto.InstructorRosterStudentDto;
import com.bridgetrack.bridgetrack.dto.InstructorSectionOptionDto;
import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.InstructorRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;

@Service
public class InstructorRosterService {

    private final InstructorRepository instructorRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public InstructorRosterService(InstructorRepository instructorRepository,
                                   SectionRepository sectionRepository,
                                   EnrollmentRepository enrollmentRepository) {
        this.instructorRepository = instructorRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Long getLoggedInInstructorId(String email) {
        return instructorRepository.findByEmailIgnoreCase(email)
                .map(instructor -> instructor.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found for email: " + email));
    }

    public List<InstructorSectionOptionDto> getInstructorSections(Long instructorId) {
        return sectionRepository.findByInstructorIdOrderBySectionIdAsc(instructorId)
                .stream()
                .sorted(Comparator
                        .comparing((Section s) -> s.getTerm() != null && s.getTerm().getTermName() != null ? s.getTerm().getTermName() : "")
                        .thenComparing(s -> s.getCourse() != null && s.getCourse().getCourseCode() != null ? s.getCourse().getCourseCode() : "")
                        .thenComparing(Section::getSectionId))
                .map(this::mapSectionToOptionDto)
                .collect(Collectors.toList());
    }

    public List<InstructorRosterStudentDto> getRosterForSection(Long instructorId, Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found."));

        if (section.getInstructorId() == null || !section.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("You do not have access to this section.");
        }

        return enrollmentRepository.findBySectionSectionId(sectionId)
                .stream()
                .filter(enrollment -> enrollment.getStudent() != null)
                .sorted(Comparator
                        .comparing((Enrollment e) -> e.getStudent().getLastName() != null ? e.getStudent().getLastName() : "")
                        .thenComparing(e -> e.getStudent().getFirstName() != null ? e.getStudent().getFirstName() : ""))
                .map(this::mapEnrollmentToRosterDto)
                .collect(Collectors.toList());
    }

    public InstructorSectionOptionDto getSectionOption(Long instructorId, Long sectionId) {
        return getInstructorSections(instructorId)
                .stream()
                .filter(section -> section.getSectionId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Section not found."));
    }

    private InstructorSectionOptionDto mapSectionToOptionDto(Section section) {
        InstructorSectionOptionDto dto = new InstructorSectionOptionDto();
        dto.setSectionId(section.getSectionId());

        String courseCode = section.getCourse() != null ? section.getCourse().getCourseCode() : "";
        String courseName = section.getCourse() != null ? section.getCourse().getCourseName() : "";
        String termName = section.getTerm() != null ? section.getTerm().getTermName() : "";
        String schedule = buildSchedule(section);

        dto.setCourseCode(courseCode);
        dto.setCourseName(courseName);
        dto.setTermName(termName);
        dto.setSchedule(schedule);
        dto.setModality(section.getModality());

        String label = "Section " + section.getSectionId();

        if (!courseCode.isBlank()) {
            label += " - " + courseCode;
        }

        if (!termName.isBlank()) {
            label += " - " + termName;
        }

        if (!schedule.isBlank()) {
            label += " - " + schedule;
        }

        dto.setOptionLabel(label);
        return dto;
    }

    private InstructorRosterStudentDto mapEnrollmentToRosterDto(Enrollment enrollment) {
        InstructorRosterStudentDto dto = new InstructorRosterStudentDto();
        dto.setStudentId(enrollment.getStudent().getStudentId());
        dto.setFirstName(enrollment.getStudent().getFirstName());
        dto.setLastName(enrollment.getStudent().getLastName());
        dto.setStudentEmail(enrollment.getStudent().getStudentEmail());
        dto.setPhone(enrollment.getStudent().getPhone());
        dto.setStatus(enrollment.getStatus() != null ? enrollment.getStatus().name() : "");
        return dto;
    }

    private String buildSchedule(Section section) {
        String meetingDays = section.getMeetingDays() != null ? section.getMeetingDays() : "";
        String startTime = formatTime(section.getStartTime());
        String endTime = formatTime(section.getEndTime());

        if (meetingDays.isBlank() && startTime.isBlank() && endTime.isBlank()) {
            return "";
        }

        if (startTime.isBlank() || endTime.isBlank()) {
            return meetingDays;
        }

        return meetingDays + " " + startTime + " - " + endTime;
    }

    private String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }

        return time.format(DateTimeFormatter.ofPattern("h:mm a"));
    }
}