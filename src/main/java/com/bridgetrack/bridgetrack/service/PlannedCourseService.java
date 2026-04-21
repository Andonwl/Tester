package com.bridgetrack.bridgetrack.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgetrack.bridgetrack.dto.PlannerSectionDto;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.EnrollmentStatus;
import com.bridgetrack.bridgetrack.model.PlannedCourse;
import com.bridgetrack.bridgetrack.model.Prerequisite;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.PlannedCourseRepository;
import com.bridgetrack.bridgetrack.repository.PrerequisiteRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Service
public class PlannedCourseService {

    private final PlannedCourseRepository plannedCourseRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    public PlannedCourseService(PlannedCourseRepository plannedCourseRepository,
                                StudentRepository studentRepository,
                                CourseRepository courseRepository,
                                SectionRepository sectionRepository,
                                EnrollmentRepository enrollmentRepository,
                                PrerequisiteRepository prerequisiteRepository) {
        this.plannedCourseRepository = plannedCourseRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    public List<PlannedCourse> getPlannedCoursesForStudent(Long studentId) {
        return plannedCourseRepository.findByStudentStudentIdOrderBySemesterLabelAscTermNumberAsc(studentId);
    }

    public List<PlannedCourse> getUnenrolledPlannedCoursesForStudent(Long studentId) {
        return plannedCourseRepository.findByStudentStudentIdAndEnrolledFalseOrderBySemesterLabelAscTermNumberAsc(studentId);
    }

    public List<PlannedCourse> getScheduledCoursesForStudent(Long studentId) {
        return plannedCourseRepository.findByStudentStudentIdAndEnrolledTrueOrderBySemesterLabelAscTermNumberAsc(studentId);
    }

    public List<String> getMissingPrerequisites(Long studentId, Long courseId) {
        List<Prerequisite> prereqs = prerequisiteRepository.findByCourse_CourseId(courseId);

        if (prereqs == null || prereqs.isEmpty()) {
            return new ArrayList<>();
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(studentId);

        List<Long> completedCourseIds = enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .map(e -> e.getSection().getCourse().getCourseId())
                .collect(Collectors.toList());

        List<String> missing = new ArrayList<>();

        for (Prerequisite prereq : prereqs) {
            Course prereqCourse = prereq.getPrerequisiteCourse();

            if (prereqCourse != null && !completedCourseIds.contains(prereqCourse.getCourseId())) {
                missing.add(prereqCourse.getCourseCode());
            }
        }

        return missing;
    }

    public boolean hasMissingPrerequisites(Long studentId, Long courseId) {
        return !getMissingPrerequisites(studentId, courseId).isEmpty();
    }

    public Map<Long, String> getCourseAvailabilityLabels(List<Course> courses) {
        Map<Long, String> availability = new LinkedHashMap<>();

        for (Course course : courses) {
            List<Section> sections = sectionRepository.findByCourseCourseId(course.getCourseId());

            if (sections == null || sections.isEmpty()) {
                availability.put(course.getCourseId(), "No sections available yet");
                continue;
            }

            Set<String> labels = new LinkedHashSet<>();

            for (Section section : sections) {
                String label = buildAvailabilityLabel(section);
                if (!label.isBlank()) {
                    labels.add(label);
                }
            }

            availability.put(
                    course.getCourseId(),
                    labels.isEmpty() ? "No sections available yet" : String.join(", ", labels)
            );
        }

        return availability;
    }

    public void addCourseToPlan(Long studentId, Long courseId, String semesterLabel, Integer termNumber) {
        boolean alreadyExists =
                plannedCourseRepository.existsByStudentStudentIdAndCourseCourseIdAndSemesterLabelAndTermNumber(
                        studentId, courseId, semesterLabel, termNumber
                );

        if (alreadyExists) {
            return;
        }

        List<String> missingPrereqs = getMissingPrerequisites(studentId, courseId);
        if (!missingPrereqs.isEmpty()) {
            throw new RuntimeException("Missing prerequisites: " + String.join(", ", missingPrereqs));
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        PlannedCourse plannedCourse = new PlannedCourse();
        plannedCourse.setStudent(student);
        plannedCourse.setCourse(course);
        plannedCourse.setSemesterLabel(semesterLabel);
        plannedCourse.setTermNumber(termNumber);
        plannedCourse.setEnrolled(false);

        plannedCourseRepository.save(plannedCourse);
    }

    public void removePlannedCourse(Long studentId, Long plannedCourseId) {
        PlannedCourse plannedCourse = plannedCourseRepository
                .findByPlannedCourseIdAndStudentStudentId(plannedCourseId, studentId)
                .orElseThrow(() -> new RuntimeException("Planned course not found"));

        plannedCourseRepository.delete(plannedCourse);
    }

    public void enrollPlannedCourse(Long studentId, Long plannedCourseId, Long sectionId) {
        PlannedCourse plannedCourse = plannedCourseRepository
                .findByPlannedCourseIdAndStudentStudentId(plannedCourseId, studentId)
                .orElseThrow(() -> new RuntimeException("Planned course not found"));

        if (plannedCourse.isEnrolled()) {
            return;
        }

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        if (section.getCourse() == null || plannedCourse.getCourse() == null) {
            throw new RuntimeException("Course data missing");
        }

        if (!section.getCourse().getCourseId().equals(plannedCourse.getCourse().getCourseId())) {
            throw new RuntimeException("Selected section does not belong to the planned course");
        }

        List<Enrollment> existingEnrollments = enrollmentRepository.findByStudentStudentId(studentId);

        String plannedTermLabel = "Term " + plannedCourse.getTermNumber();

        for (Enrollment existingEnrollment : existingEnrollments) {
            if (existingEnrollment.getSection() == null) {
                continue;
            }

            if (existingEnrollment.getPlannedTerm() != null
                    && existingEnrollment.getPlannedTerm().toLowerCase().contains(plannedTermLabel.toLowerCase())
                    && schedulesConflict(section, existingEnrollment.getSection())) {
                throw new RuntimeException("Schedule conflict with another selected section in this term.");
            }
        }

        boolean alreadyEnrolled = enrollmentRepository
                .findByStudentStudentIdAndSectionSectionId(studentId, sectionId)
                .isPresent();

        if (alreadyEnrolled) {
            plannedCourse.setEnrolled(true);
            plannedCourseRepository.save(plannedCourse);
            return;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(plannedCourse.getStudent());
        enrollment.setSection(section);
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setPlannedTerm(plannedCourse.getSemesterLabel() + " - Term " + plannedCourse.getTermNumber());

        enrollmentRepository.save(enrollment);

        plannedCourse.setEnrolled(true);
        plannedCourseRepository.save(plannedCourse);
    }

    /**
     * NEW: When a student drops a section from My Schedule, we delete the Enrollment row.
     * But the Course Planner counters come from PlannedCourse.enrolled.
     *
     * This helper method re-syncs the planner state by setting enrolled=false for the matching planned course.
     *
     * This method expects that the caller is doing drop logic elsewhere; it only updates planner state.
     *
     * Requires repository support:
     * - EnrollmentRepository.findByStudentStudentEmailAndSectionSectionId(...)
     * - PlannedCourseRepository.findFirstByStudentStudentIdAndCourseCourseIdAndEnrolledTrueOrderBySemesterLabelDescTermNumberDesc(...)
     */
    @Transactional
    public void markPlannedCourseUnenrolledFromDroppedSection(String studentEmail, Long sectionId) {
        if (studentEmail == null || studentEmail.isBlank()) {
            throw new RuntimeException("Student email is missing.");
        }
        if (sectionId == null) {
            throw new RuntimeException("Section not found.");
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentStudentEmailAndSectionSectionId(studentEmail, sectionId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found for this section."));

        Long studentId = enrollment.getStudent() != null ? enrollment.getStudent().getStudentId() : null;
        Long courseId = (enrollment.getSection() != null && enrollment.getSection().getCourse() != null)
                ? enrollment.getSection().getCourse().getCourseId()
                : null;

        if (studentId == null || courseId == null) {
            // If we can't resolve, don't silently succeed — otherwise the counter bug persists.
            throw new RuntimeException("Could not resolve planned course to update for dropped enrollment.");
        }

        plannedCourseRepository
                .findFirstByStudentStudentIdAndCourseCourseIdAndEnrolledTrueOrderBySemesterLabelDescTermNumberDesc(studentId, courseId)
                .ifPresent(pc -> {
                    pc.setEnrolled(false);
                    plannedCourseRepository.save(pc);
                });
    }

    public Map<Long, List<PlannerSectionDto>> getSectionsByCourse(List<PlannedCourse> plannedCourses, Long studentId) {
        Map<Long, List<PlannerSectionDto>> sectionsByCourse = new LinkedHashMap<>();

        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(studentId);

        for (PlannedCourse plannedCourse : plannedCourses) {
            Long courseId = plannedCourse.getCourse().getCourseId();

            if (!sectionsByCourse.containsKey(courseId)) {
                List<Section> sections = sectionRepository.findByCourseCourseId(courseId);

                List<PlannerSectionDto> dtoList = sections.stream()
                        .map(section -> {
                            PlannerSectionDto dto = new PlannerSectionDto();

                            dto.setId(section.getSectionId());
                            dto.setSectionCode(buildSectionCode(section));
                            dto.setSchedule(buildSchedule(section));
                            dto.setModality(section.getModality());
                            dto.setTerm(resolveTermName(section));
                            dto.setCourseId(section.getCourse().getCourseId());
                            dto.setCourseName(section.getCourse().getCourseName());
                            dto.setInstructorName(
                                    section.getInstructorId() != null
                                            ? String.valueOf(section.getInstructorId())
                                            : "TBD"
                            );

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
                                    String plannedTerm = enrollment.getPlannedTerm() != null
                                            ? enrollment.getPlannedTerm().toLowerCase()
                                            : "";

                                    if (plannedTerm.contains("term 1")) {
                                        conflictTerm1 = true;
                                    }

                                    if (plannedTerm.contains("term 2")) {
                                        conflictTerm2 = true;
                                    }
                                }
                            }

                            dto.setConflictTerm1(conflictTerm1);
                            dto.setConflictTerm2(conflictTerm2);

                            return dto;
                        })
                        .collect(Collectors.toList());

                sectionsByCourse.put(courseId, dtoList);
            }
        }

        return sectionsByCourse;
    }

    public List<SemesterGroup> groupPlannedCoursesBySemester(List<PlannedCourse> plannedCourses) {
        Map<String, SemesterGroup> grouped = new LinkedHashMap<>();

        for (PlannedCourse plannedCourse : plannedCourses) {
            String semesterLabel = plannedCourse.getSemesterLabel();
            Integer termNumber = plannedCourse.getTermNumber();

            SemesterGroup semesterGroup = grouped.computeIfAbsent(
                    semesterLabel,
                    this::createSemesterGroup
            );

            if (termNumber != null && termNumber == 2) {
                semesterGroup.getTerms().get(1).getCourses().add(plannedCourse);
            } else {
                semesterGroup.getTerms().get(0).getCourses().add(plannedCourse);
            }
        }

        for (SemesterGroup semesterGroup : grouped.values()) {
            int total =
                    semesterGroup.getTerms().get(0).getCourses().size()
                  + semesterGroup.getTerms().get(1).getCourses().size();

            semesterGroup.setTotalPlannedLabel(total + " planned course" + (total == 1 ? "" : "s"));
        }

        return new ArrayList<>(grouped.values());
    }

    private SemesterGroup createSemesterGroup(String semesterLabel) {
        SemesterGroup semesterGroup = new SemesterGroup();
        semesterGroup.setLabel(semesterLabel);
        semesterGroup.setTotalPlannedLabel("0 planned courses");

        String season = semesterLabel.startsWith("Fall") ? "Fall" : "Spring";

        TermGroup term1 = new TermGroup();
        term1.setLabel(season + " Term 1");
        term1.setTermCssClass(" dot-t1");

        TermGroup term2 = new TermGroup();
        term2.setLabel(season + " Term 2");
        term2.setTermCssClass(" dot-t2");

        semesterGroup.getTerms().add(term1);
        semesterGroup.getTerms().add(term2);

        return semesterGroup;
    }

    public List<SemesterGroup> buildPlannerDisplaySemesters(List<PlannedCourse> plannedCourses) {
        return buildPlannerDisplaySemesters(plannedCourses, "Spring 2026", 4);
    }

    public List<SemesterGroup> buildPlannerDisplaySemesters(List<PlannedCourse> plannedCourses,
                                                            String startingSemester,
                                                            Integer semesterCount) {
        List<String> semesterLabels = generateSemesterLabels(startingSemester, semesterCount);

        Map<String, SemesterGroup> grouped = new LinkedHashMap<>();

        for (String semesterLabel : semesterLabels) {
            grouped.put(semesterLabel, createSemesterGroup(semesterLabel));
        }

        for (PlannedCourse plannedCourse : plannedCourses) {
            String semesterLabel = plannedCourse.getSemesterLabel();
            Integer termNumber = plannedCourse.getTermNumber();

            SemesterGroup semesterGroup = grouped.computeIfAbsent(
                    semesterLabel,
                    this::createSemesterGroup
            );

            if (termNumber != null && termNumber == 2) {
                semesterGroup.getTerms().get(1).getCourses().add(plannedCourse);
            } else {
                semesterGroup.getTerms().get(0).getCourses().add(plannedCourse);
            }
        }

        for (SemesterGroup semesterGroup : grouped.values()) {
            int total =
                    semesterGroup.getTerms().get(0).getCourses().size()
                  + semesterGroup.getTerms().get(1).getCourses().size();

            semesterGroup.setTotalPlannedLabel(total + " course" + (total == 1 ? "" : "s") + " planned");
        }

        return new ArrayList<>(grouped.values());
    }

    private List<String> generateSemesterLabels(String startingSemester, Integer semesterCount) {
        List<String> semesterLabels = new ArrayList<>();

        if (semesterCount == null || semesterCount < 1) {
            semesterCount = 4;
        }

        String[] parts = startingSemester.split(" ");
        String season = parts[0];
        int year = Integer.parseInt(parts[1]);

        for (int i = 0; i < semesterCount; i++) {
            semesterLabels.add(season + " " + year);

            if ("Spring".equals(season)) {
                season = "Fall";
            } else {
                season = "Spring";
                year++;
            }
        }

        return semesterLabels;
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

    private String buildAvailabilityLabel(Section section) {
        if (section == null) {
            return "";
        }

        String semesterLabel = "";
        if (section.getTerm() != null && section.getTerm().getTermName() != null) {
            semesterLabel = section.getTerm().getTermName().trim();
        }

        String termName = resolveTermName(section);

        if (!semesterLabel.isBlank() && !termName.isBlank()
                && !semesterLabel.equalsIgnoreCase(termName)) {
            return semesterLabel + " - " + termName;
        }

        if (!semesterLabel.isBlank()) {
            return semesterLabel;
        }

        if (!termName.isBlank()) {
            return termName;
        }

        return "";
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

    private boolean timesOverlap(LocalTime start1,
                                 LocalTime end1,
                                 LocalTime start2,
                                 LocalTime end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    public static class SemesterGroup {
        private String label;
        private String totalPlannedLabel;
        private List<TermGroup> terms = new ArrayList<>();

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getTotalPlannedLabel() {
            return totalPlannedLabel;
        }

        public void setTotalPlannedLabel(String totalPlannedLabel) {
            this.totalPlannedLabel = totalPlannedLabel;
        }

        public List<TermGroup> getTerms() {
            return terms;
        }

        public void setTerms(List<TermGroup> terms) {
            this.terms = terms;
        }
    }

    public static class TermGroup {
        private String label;
        private String termCssClass;
        private List<PlannedCourse> courses = new ArrayList<>();

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getTermCssClass() {
            return termCssClass;
        }

        public void setTermCssClass(String termCssClass) {
            this.termCssClass = termCssClass;
        }

        public List<PlannedCourse> courses() {
            return courses;
        }

        public List<PlannedCourse> getCourses() {
            return courses;
        }

        public void setCourses(List<PlannedCourse> courses) {
            this.courses = courses;
        }
    }
}