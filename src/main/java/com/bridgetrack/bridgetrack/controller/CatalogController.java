package com.bridgetrack.bridgetrack.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgetrack.bridgetrack.dto.CourseCatalog;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Program;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.ProgramRepository;

@RestController
public class CatalogController {

    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;

    public CatalogController(CourseRepository courseRepository,
                             ProgramRepository programRepository) {
        this.courseRepository = courseRepository;
        this.programRepository = programRepository;
    }

    @GetMapping("/api/catalog/courses")
    public List<CourseCatalog> getCourseCatalog(
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) String modality,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean requiresPrereq,
            @RequestParam(required = false) String search
    ) {
        List<Course> courses = courseRepository.findAllByOrderByCourseCodeAsc();

        Stream<Course> stream = courses.stream();

        if (programId != null) {
            stream = stream.filter(c ->
                c.getProgram() != null &&
                c.getProgram().getProgramId() != null &&
                c.getProgram().getProgramId().equals(programId)
            );
        }

        if (modality != null && !modality.isBlank()) {
            String modalityLower = modality.trim().toLowerCase(Locale.ROOT);
            stream = stream.filter(c ->
                c.getModality() != null &&
                c.getModality().trim().toLowerCase(Locale.ROOT).equals(modalityLower)
            );
        }

        if (category != null && !category.isBlank()) {
            String categoryLower = category.trim().toLowerCase(Locale.ROOT);
            stream = stream.filter(c ->
                c.getCategory() != null &&
                c.getCategory().trim().toLowerCase(Locale.ROOT).equals(categoryLower)
            );
        }

        if (requiresPrereq != null) {
            stream = stream.filter(c -> c.isRequiresPrereq() == requiresPrereq);
        }

        if (search != null && !search.isBlank()) {
            String searchLower = search.trim().toLowerCase(Locale.ROOT);
            stream = stream.filter(c ->
                (c.getCourseCode() != null && c.getCourseCode().toLowerCase(Locale.ROOT).contains(searchLower)) ||
                (c.getCourseName() != null && c.getCourseName().toLowerCase(Locale.ROOT).contains(searchLower)) ||
                (c.getDescription() != null && c.getDescription().toLowerCase(Locale.ROOT).contains(searchLower))
            );
        }

        return stream.map(c -> {
            Program p = c.getProgram();

            Long mappedProgramId = (p == null) ? null : p.getProgramId();
            String programName = (p == null) ? null : p.getProgramName();

            return new CourseCatalog(
                    c.getCourseId(),
                    c.getCourseCode(),
                    c.getCourseName(),
                    c.getDescription(),
                    c.getModality(),
                    c.isRequiresPrereq(),
                    c.getCategory(),
                    mappedProgramId,
                    programName
            );
        }).toList();
    }

    @GetMapping("/api/catalog/modalities")
    public List<String> getModalities() {
        return courseRepository.findAllByOrderByCourseCodeAsc().stream()
                .map(Course::getModality)
                .filter(m -> m != null && !m.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    @GetMapping("/api/catalog/categories")
    public List<String> getCategories() {
        return courseRepository.findAllByOrderByCourseCodeAsc().stream()
                .map(Course::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    @GetMapping("/api/catalog/programs")
    public List<ProgramFilterOption> getPrograms() {
        return programRepository.findAll().stream()
                .map(p -> new ProgramFilterOption(p.getProgramId(), p.getProgramName()))
                .sorted((a, b) -> a.getProgramName().compareToIgnoreCase(b.getProgramName()))
                .toList();
    }

    public static class ProgramFilterOption {
        private Long programId;
        private String programName;

        public ProgramFilterOption(Long programId, String programName) {
            this.programId = programId;
            this.programName = programName;
        }

        public Long getProgramId() {
            return programId;
        }

        public String getProgramName() {
            return programName;
        }
    }
}