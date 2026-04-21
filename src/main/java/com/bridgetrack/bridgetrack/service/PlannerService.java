package com.bridgetrack.bridgetrack.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgetrack.bridgetrack.dto.PlannerCourseDto;
import com.bridgetrack.bridgetrack.dto.PlannerEnrollmentDto;
import com.bridgetrack.bridgetrack.dto.PlannerEnrollmentRequestDto;
import com.bridgetrack.bridgetrack.dto.PlannerPageDto;
import com.bridgetrack.bridgetrack.dto.PlannerResponseDto;
import com.bridgetrack.bridgetrack.dto.PlannerSectionDto;
import com.bridgetrack.bridgetrack.dto.PlannerStudentDto;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Service
@Transactional
public class PlannerService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public PlannerService(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            SectionRepository sectionRepository,
            EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public PlannerPageDto getPlannerPage(String email) {
        Student student = studentRepository.findByStudentEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        PlannerStudentDto studentDto = getPlannerStudent(student.getStudentId());
        List<PlannerCourseDto> courses = getCourses();
        List<PlannerSectionDto> sections = getSections(student.getStudentId());
        PlannerResponseDto planner = getPlanner(student.getStudentId());

        PlannerPageDto dto = new PlannerPageDto();
        dto.setStudent(studentDto);
        dto.setCourses(courses);
        dto.setSections(sections);
        dto.setTerm1(planner.getTerm1());
        dto.setTerm2(planner.getTerm2());

        return dto;
    }

    public void resetPlanner(String email) {
        Student student = studentRepository.findByStudentEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(student.getStudentId());
        enrollmentRepository.deleteAll(enrollments);
    }

    public void addEnrollmentForLoggedInStudent(String email, PlannerEnrollmentRequestDto request) {
        Student student = studentRepository.findByStudentEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        request.setStudentId(student.getStudentId());
        addEnrollment(student.getStudentId(), request);
    }

    public void removeEnrollmentForLoggedInStudent(String email, Long sectionId) {
        Student student = studentRepository.findByStudentEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        removeEnrollment(student.getStudentId(), sectionId);
    }

    public PlannerStudentDto getPlannerStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        PlannerStudentDto dto = new PlannerStudentDto();
        dto.setId(student.getStudentId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getStudentEmail());
        dto.setCompletedCourses(new ArrayList<>());

        return dto;
    }

    public List<PlannerCourseDto> getCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapCourseToDto)
                .collect(Collectors.toList());
    }

    public List<PlannerSectionDto> getSections(Long studentId) {
        List<Section> sections = sectionRepository.findAll();
        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(studentId);

        return sections.stream()
                .map(section -> {
                    PlannerSectionDto dto = mapSectionToDto(section);

                    boolean conflictTerm1 = false;
                    boolean conflictTerm2 = false;

                    for (Enrollment enrollment : enrollments) {
                        Section existing = enrollment.getSection();

                        if (existing == null) {
                            continue;
                        }

                        if (section.getSectionId() != null
                                && existing.getSectionId() != null
                                && section.getSectionId().equals(existing.getSectionId())) {
                            continue;
                        }

                        if (schedulesConflict(section, existing)) {
                            if ("Term 1".equalsIgnoreCase(enrollment.getPlannedTerm())) {
                                conflictTerm1 = true;
                            }

                            if ("Term 2".equalsIgnoreCase(enrollment.getPlannedTerm())) {
                                conflictTerm2 = true;
                            }
                        }
                    }

                    dto.setConflictTerm1(conflictTerm1);
                    dto.setConflictTerm2(conflictTerm2);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public PlannerResponseDto getPlanner(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(studentId);

        List<PlannerEnrollmentDto> term1 = enrollments.stream()
                .filter(e -> "Term 1".equalsIgnoreCase(e.getPlannedTerm()))
                .map(this::mapEnrollmentToDto)
                .collect(Collectors.toList());

        List<PlannerEnrollmentDto> term2 = enrollments.stream()
                .filter(e -> "Term 2".equalsIgnoreCase(e.getPlannedTerm()))
                .map(this::mapEnrollmentToDto)
                .collect(Collectors.toList());

        PlannerResponseDto response = new PlannerResponseDto();
        response.setStudentId(studentId);
        response.setTerm1(term1);
        response.setTerm2(term2);

        return response;
    }

    public void addEnrollment(Long studentId, PlannerEnrollmentRequestDto request) {
        if (request == null) {
            throw new RuntimeException("Enrollment request is required.");
        }

        if (request.getStudentId() == null || !studentId.equals(request.getStudentId())) {
            throw new RuntimeException("Student ID mismatch.");
        }

        if (request.getSectionId() == null) {
            throw new RuntimeException("Section ID is required.");
        }

        if (request.getPlannedTerm() == null || request.getPlannedTerm().isBlank()) {
            throw new RuntimeException("Term is required.");
        }

        String requestedTerm = request.getPlannedTerm().trim();
        if (!"Term 1".equalsIgnoreCase(requestedTerm) && !"Term 2".equalsIgnoreCase(requestedTerm)) {
            throw new RuntimeException("Invalid term.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found."));

        Course course = section.getCourse();
        if (course == null) {
            throw new RuntimeException("Section is missing course information.");
        }

        List<Enrollment> existingEnrollments = enrollmentRepository.findByStudentStudentId(studentId);

        boolean duplicateSection = existingEnrollments.stream()
                .anyMatch(e -> e.getSection() != null
                        && e.getSection().getSectionId() != null
                        && e.getSection().getSectionId().equals(section.getSectionId()));

        if (duplicateSection) {
            throw new RuntimeException("Student is already enrolled in this section.");
        }

        boolean duplicateCourse = existingEnrollments.stream()
                .anyMatch(e -> e.getSection() != null
                        && e.getSection().getCourse() != null
                        && e.getSection().getCourse().getCourseCode() != null
                        && course.getCourseCode() != null
                        && e.getSection().getCourse().getCourseCode().equalsIgnoreCase(course.getCourseCode()));

        if (duplicateCourse) {
            throw new RuntimeException("Student is already enrolled or planned for this course.");
        }

        List<Enrollment> sameTermEnrollments = existingEnrollments.stream()
                .filter(e -> requestedTerm.equalsIgnoreCase(e.getPlannedTerm()))
                .collect(Collectors.toList());

        for (Enrollment existing : sameTermEnrollments) {
            Section existingSection = existing.getSection();
            if (existingSection != null && schedulesConflict(section, existingSection)) {
                throw new RuntimeException("Schedule conflict with another selected section in this term.");
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setPlannedTerm(requestedTerm);
        enrollment.setEnrolledAt(LocalDateTime.now());

        enrollmentRepository.save(enrollment);
    }

    public void removeEnrollment(Long studentId, Long sectionId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found."));

        Optional<Enrollment> enrollmentOpt =
                enrollmentRepository.findByStudentStudentIdAndSectionSectionId(studentId, sectionId);

        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found.");
        }

        Enrollment enrollment = enrollmentOpt.get();
        String removedTerm = enrollment.getPlannedTerm();

        enrollmentRepository.delete(enrollment);

        if ("Term 1".equalsIgnoreCase(removedTerm)) {
            List<Enrollment> remaining = enrollmentRepository.findByStudentStudentId(studentId);
            boolean hasAnyTerm1 = remaining.stream()
                    .anyMatch(e -> "Term 1".equalsIgnoreCase(e.getPlannedTerm()));

            if (!hasAnyTerm1) {
                List<Enrollment> term2Enrollments = remaining.stream()
                        .filter(e -> "Term 2".equalsIgnoreCase(e.getPlannedTerm()))
                        .collect(Collectors.toList());

                enrollmentRepository.deleteAll(term2Enrollments);
            }
        }
    }

    private PlannerCourseDto mapCourseToDto(Course course) {
        PlannerCourseDto dto = new PlannerCourseDto();
        dto.setId(course.getCourseId());
        dto.setCourseCode(course.getCourseCode());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(3);

        if (course.getProgram() != null) {
            dto.setProgramName(course.getProgram().getProgramName());
        } else {
            dto.setProgramName("General");
        }

        dto.setPrerequisites(new ArrayList<>());
        return dto;
    }

    private PlannerSectionDto mapSectionToDto(Section section) {
        PlannerSectionDto dto = new PlannerSectionDto();
        dto.setId(section.getSectionId());
        dto.setSectionCode(buildSectionCode(section));
        dto.setSchedule(buildSchedule(section));
        dto.setModality(section.getModality());
        dto.setTerm(resolveTermName(section));

        if (section.getCourse() != null) {
            dto.setCourseId(section.getCourse().getCourseId());
            dto.setCourseName(section.getCourse().getCourseName());
        }

        dto.setInstructorName(
                section.getInstructorId() != null
                        ? String.valueOf(section.getInstructorId())
                        : "TBD"
        );

        return dto;
    }

    private PlannerEnrollmentDto mapEnrollmentToDto(Enrollment enrollment) {
        PlannerEnrollmentDto dto = new PlannerEnrollmentDto();

        Section section = enrollment.getSection();
        Course course = section != null ? section.getCourse() : null;

        if (section != null) {
            dto.setSectionId(section.getSectionId());
            dto.setSectionCode(buildSectionCode(section));
            dto.setSchedule(buildSchedule(section));
            dto.setModality(section.getModality());
            dto.setInstructorName(
                    section.getInstructorId() != null
                            ? String.valueOf(section.getInstructorId())
                            : "TBD"
            );
        }

        if (course != null) {
            dto.setCourseId(course.getCourseId());
            dto.setCourseCode(course.getCourseCode());
            dto.setCourseName(course.getCourseName());
            dto.setCredits(3);
        }

        dto.setTerm(enrollment.getPlannedTerm());
        return dto;
    }

    private String buildSectionCode(Section section) {
        if (section.getCourse() != null && section.getCourse().getCourseCode() != null) {
            return section.getCourse().getCourseCode() + "-" + section.getSectionId();
        }
        return String.valueOf(section.getSectionId());
    }

    private String resolveTermName(Section section) {
        if (section.getTerm() == null) {
            return "";
        }

        String name = section.getTerm().getTermName();
        if (name == null || name.isBlank()) {
            return "";
        }

        if (name.toLowerCase().contains("spring")) {
            return "Term 1";
        }

        if (name.toLowerCase().contains("summer")) {
            return "Term 2";
        }

        return name;
    }

    private String buildSchedule(Section section) {
        if (section == null) {
            return "";
        }

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return time.format(formatter);
    }

    private boolean schedulesConflict(Section a, Section b) {
        if (a == null || b == null) {
            return false;
        }

        if (!daysOverlap(a.getMeetingDays(), b.getMeetingDays())) {
            return false;
        }

        return timesOverlap(
                a.getStartTime(), a.getEndTime(),
                b.getStartTime(), b.getEndTime()
        );
    }

    private boolean daysOverlap(String days1, String days2) {
        if (days1 == null || days2 == null) {
            return false;
        }

        List<String> d1 = List.of(days1.split("/"))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        List<String> d2 = List.of(days2.split("/"))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        for (String day : d1) {
            if (d2.stream().anyMatch(d -> d.equalsIgnoreCase(day))) {
                return true;
            }
        }

        return false;
    }

    private boolean timesOverlap(
            LocalTime start1,
            LocalTime end1,
            LocalTime start2,
            LocalTime end2) {

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}