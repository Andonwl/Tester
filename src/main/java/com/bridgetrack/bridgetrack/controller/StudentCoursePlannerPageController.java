package com.bridgetrack.bridgetrack.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bridgetrack.bridgetrack.dto.PlannerSectionDto;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.PlannedCourse;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.ProgramRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;
import com.bridgetrack.bridgetrack.service.PlannedCourseService;

@Controller
@RequestMapping("/student/course-planner")
public class StudentCoursePlannerPageController {

    private final PlannedCourseService plannedCourseService;
    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public StudentCoursePlannerPageController(PlannedCourseService plannedCourseService,
                                              ProgramRepository programRepository,
                                              CourseRepository courseRepository,
                                              StudentRepository studentRepository) {
        this.plannedCourseService = plannedCourseService;
        this.programRepository = programRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public String showCoursePlanner(
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false, defaultValue = "ALL") String modality,
            @RequestParam(required = false, defaultValue = "ALL") String planStatus,
            @RequestParam(required = false, defaultValue = "ALL") String prereqStatus,
            @RequestParam(defaultValue = "Spring 2026") String startingSemester,
            @RequestParam(defaultValue = "4") Integer semesterCount,
            @RequestParam(defaultValue = "planner") String activeTab,
            Model model,
            Authentication authentication) {

        Long studentId = getLoggedInStudentId(authentication);

        List<PlannedCourse> plannedCourses = plannedCourseService.getPlannedCoursesForStudent(studentId);
        List<PlannedCourse> scheduledCourses = plannedCourseService.getScheduledCoursesForStudent(studentId);

        List<Course> courses = (programId != null)
                ? courseRepository.findByProgram_ProgramIdOrderByCourseCodeAsc(programId)
                : courseRepository.findAllByOrderByCourseCodeAsc();

        if (modality != null && !"ALL".equalsIgnoreCase(modality)) {
            courses = courses.stream()
                    .filter(course -> course.getModality() != null
                            && course.getModality().equalsIgnoreCase(modality))
                    .toList();
        }

        Map<Long, List<String>> missingPrereqs = new HashMap<>();
        for (Course course : courses) {
            List<String> missing = plannedCourseService.getMissingPrerequisites(studentId, course.getCourseId());
            missingPrereqs.put(course.getCourseId(), missing);
        }

        if (planStatus != null && !"ALL".equalsIgnoreCase(planStatus)) {
            courses = courses.stream()
                    .filter(course -> {
                        boolean isPlanned = plannedCourses.stream()
                                .anyMatch(pc -> pc.getCourse() != null
                                        && pc.getCourse().getCourseId().equals(course.getCourseId()));

                        boolean isEnrolled = scheduledCourses.stream()
                                .anyMatch(pc -> pc.getCourse() != null
                                        && pc.getCourse().getCourseId().equals(course.getCourseId()));

                        return switch (planStatus.toUpperCase()) {
                            case "PLANNED" -> isPlanned && !isEnrolled;
                            case "ENROLLED" -> isEnrolled;
                            case "NOT_ADDED" -> !isPlanned && !isEnrolled;
                            default -> true;
                        };
                    })
                    .toList();
        }

        if (prereqStatus != null && !"ALL".equalsIgnoreCase(prereqStatus)) {
            courses = courses.stream()
                    .filter(course -> {
                        List<String> missing = missingPrereqs.get(course.getCourseId());
                        boolean hasMissing = missing != null && !missing.isEmpty();

                        return switch (prereqStatus.toUpperCase()) {
                            case "MET" -> !hasMissing;
                            case "MISSING" -> hasMissing;
                            default -> true;
                        };
                    })
                    .toList();
        }

        Map<Long, String> courseAvailability = plannedCourseService.getCourseAvailabilityLabels(courses);

        model.addAttribute("programs", programRepository.findAll());
        model.addAttribute("courses", courses);
        model.addAttribute("plannedCourses", plannedCourses);
        model.addAttribute("scheduledCourses", scheduledCourses);
        model.addAttribute("missingPrereqs", missingPrereqs);
        model.addAttribute("courseAvailability", courseAvailability);

        model.addAttribute("selectedProgramId", programId);
        model.addAttribute("selectedModality", modality);
        model.addAttribute("selectedPlanStatus", planStatus);
        model.addAttribute("selectedPrereqStatus", prereqStatus);
        model.addAttribute("selectedStartingSemester", startingSemester);
        model.addAttribute("selectedSemesterCount", semesterCount);
        model.addAttribute("activeTab", activeTab);

        return "course-planner";
    }

    @PostMapping("/add")
    public String addCourse(@RequestParam Long courseId,
                            @RequestParam String semesterLabel,
                            @RequestParam Integer termNumber,
                            @RequestParam(required = false) Long programId,
                            @RequestParam(required = false, defaultValue = "ALL") String modality,
                            @RequestParam(required = false, defaultValue = "ALL") String planStatus,
                            @RequestParam(required = false, defaultValue = "ALL") String prereqStatus,
                            @RequestParam(defaultValue = "Spring 2026") String startingSemester,
                            @RequestParam(defaultValue = "4") Integer semesterCount,
                            @RequestParam(defaultValue = "planner") String activeTab,
                            RedirectAttributes redirectAttributes,
                            Authentication authentication) {

        Long studentId = getLoggedInStudentId(authentication);

        try {
            plannedCourseService.addCourseToPlan(studentId, courseId, semesterLabel, termNumber);
            redirectAttributes.addFlashAttribute("successMessage", "Course added to plan.");

            // NEW: go straight to section selection after successful add
            return "redirect:/student/course-planner/sections";

        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return buildPlannerRedirectUrl(programId, modality, planStatus, prereqStatus, startingSemester, semesterCount, activeTab);
        }
    }

    @PostMapping("/remove")
    public String removeCourse(@RequestParam Long plannedCourseId,
                               @RequestParam(required = false) Long programId,
                               @RequestParam(required = false, defaultValue = "ALL") String modality,
                               @RequestParam(required = false, defaultValue = "ALL") String planStatus,
                               @RequestParam(required = false, defaultValue = "ALL") String prereqStatus,
                               @RequestParam(defaultValue = "Spring 2026") String startingSemester,
                               @RequestParam(defaultValue = "4") Integer semesterCount,
                               @RequestParam(defaultValue = "planner") String activeTab,
                               RedirectAttributes redirectAttributes,
                               Authentication authentication) {

        Long studentId = getLoggedInStudentId(authentication);

        plannedCourseService.removePlannedCourse(studentId, plannedCourseId);
        redirectAttributes.addFlashAttribute("successMessage", "Course removed from plan.");

        return buildPlannerRedirectUrl(programId, modality, planStatus, prereqStatus, startingSemester, semesterCount, activeTab);
    }

    @GetMapping("/sections")
    public String showSectionSelection(Model model, Authentication authentication) {
        Long studentId = getLoggedInStudentId(authentication);

        List<PlannedCourse> plannedCourses = plannedCourseService.getUnenrolledPlannedCoursesForStudent(studentId);
        Map<Long, List<PlannerSectionDto>> sectionsByCourse =
                plannedCourseService.getSectionsByCourse(plannedCourses, studentId);

        model.addAttribute("plannedSemesters",
                plannedCourseService.groupPlannedCoursesBySemester(plannedCourses));
        model.addAttribute("sectionsByCourse", sectionsByCourse);

        return "section-selection";
    }

    @PostMapping("/sections/enroll")
    public String enrollFromSectionSelection(@RequestParam Long plannedCourseId,
                                             @RequestParam Long sectionId,
                                             RedirectAttributes redirectAttributes,
                                             Authentication authentication) {

        Long studentId = getLoggedInStudentId(authentication);

        try {
            plannedCourseService.enrollPlannedCourse(studentId, plannedCourseId, sectionId);
            redirectAttributes.addFlashAttribute("successMessage", "Course enrolled successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/student/course-planner/sections";
    }

    private Long getLoggedInStudentId(Authentication authentication) {
        String email = authentication.getName();

        Student student = studentRepository.findByStudentEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged-in student not found."));

        return student.getStudentId();
    }

    private String buildPlannerRedirectUrl(Long programId,
                                           String modality,
                                           String planStatus,
                                           String prereqStatus,
                                           String startingSemester,
                                           Integer semesterCount,
                                           String activeTab) {
        StringBuilder url = new StringBuilder("redirect:/student/course-planner?");
        url.append("startingSemester=").append(startingSemester.replace(" ", "%20"));
        url.append("&semesterCount=").append(semesterCount);
        url.append("&activeTab=").append(activeTab);

        if (programId != null) {
            url.append("&programId=").append(programId);
        }

        if (modality != null && !modality.isBlank()) {
            url.append("&modality=").append(modality.replace(" ", "%20"));
        }

        if (planStatus != null && !planStatus.isBlank()) {
            url.append("&planStatus=").append(planStatus);
        }

        if (prereqStatus != null && !prereqStatus.isBlank()) {
            url.append("&prereqStatus=").append(prereqStatus);
        }

        return url.toString();
    }
}