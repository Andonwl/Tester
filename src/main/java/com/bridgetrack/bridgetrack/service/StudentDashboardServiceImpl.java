package com.bridgetrack.bridgetrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.dto.StudentDashboardDto;
import com.bridgetrack.bridgetrack.dto.StudentUpcomingClassDto;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.PlannedCourse;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.PlannedCourseRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Service
public class StudentDashboardServiceImpl implements StudentDashboardService {

    private final StudentRepository studentRepository;
    private final PlannedCourseRepository plannedCourseRepository;

    public StudentDashboardServiceImpl(StudentRepository studentRepository,
                                       PlannedCourseRepository plannedCourseRepository) {
        this.studentRepository = studentRepository;
        this.plannedCourseRepository = plannedCourseRepository;
    }

    @Override
    public StudentDashboardDto getDashboardData(String studentEmail) {
        Student student = studentRepository.findByStudentEmailIgnoreCase(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentEmail));

        List<PlannedCourse> enrolledPlannedCourses =
                plannedCourseRepository.findByStudentStudentIdAndEnrolledTrueOrderBySemesterLabelAscTermNumberAscPlannedCourseIdAsc(student.getStudentId());

        if (enrolledPlannedCourses.isEmpty()) {
            return new StudentDashboardDto("Upcoming Term", null, List.of());
        }

        PlannedCourse firstUpcoming = enrolledPlannedCourses.get(0);
        String semesterLabel = firstUpcoming.getSemesterLabel();
        Integer termNumber = firstUpcoming.getTermNumber();

        List<StudentUpcomingClassDto> upcomingClasses = enrolledPlannedCourses.stream()
                .filter(pc -> sameSemesterAndTerm(pc, semesterLabel, termNumber))
                .map(this::toDto)
                .toList();

        String heading = buildHeading(semesterLabel, termNumber);

        return new StudentDashboardDto(heading, null, upcomingClasses);
    }

    private boolean sameSemesterAndTerm(PlannedCourse plannedCourse, String semesterLabel, Integer termNumber) {
        boolean sameSemester = false;
        boolean sameTerm = false;

        if (semesterLabel == null && plannedCourse.getSemesterLabel() == null) {
            sameSemester = true;
        } else if (semesterLabel != null) {
            sameSemester = semesterLabel.equalsIgnoreCase(plannedCourse.getSemesterLabel());
        }

        if (termNumber == null && plannedCourse.getTermNumber() == null) {
            sameTerm = true;
        } else if (termNumber != null) {
            sameTerm = termNumber.equals(plannedCourse.getTermNumber());
        }

        return sameSemester && sameTerm;
    }

    private StudentUpcomingClassDto toDto(PlannedCourse plannedCourse) {
        Course course = plannedCourse.getCourse();

        return new StudentUpcomingClassDto(
                plannedCourse.getPlannedCourseId(),
                course != null ? valueOrBlank(course.getCourseCode()) : "",
                course != null ? valueOrBlank(course.getCourseName()) : "",
                valueOrBlank(plannedCourse.getSemesterLabel()),
                plannedCourse.getTermNumber()
        );
    }

    private String buildHeading(String semesterLabel, Integer termNumber) {
        String semester = semesterLabel != null ? semesterLabel : "Upcoming Semester";
        String term = termNumber != null ? " - Term " + termNumber : "";
        return semester + term;
    }

    private String valueOrBlank(String value) {
        return value != null ? value : "";
    }
}