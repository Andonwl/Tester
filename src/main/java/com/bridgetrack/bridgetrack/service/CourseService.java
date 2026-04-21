package com.bridgetrack.bridgetrack.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bridgetrack.bridgetrack.dto.CourseRequest;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Program;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.PrerequisiteRepository;
import com.bridgetrack.bridgetrack.repository.ProgramRepository;

import jakarta.transaction.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ProgramRepository programRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    public CourseService(CourseRepository courseRepository, ProgramRepository programRepository, PrerequisiteRepository prerequisiteRepository) {
        this.courseRepository = courseRepository;
        this.programRepository = programRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    @Transactional
    public Course create(CourseRequest req) {
        // validate required fields
        String code = req.getCourseCode() != null ? req.getCourseCode().trim() : null;
        String name = req.getCourseName() != null ? req.getCourseName().trim() : null;
        String category = req.getCategory() != null ? req.getCategory().trim() : null;

        if (code == null || code.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course code is required.");
        if (name == null || name.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course name is required.");
        if (req.getProgramId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program is required.");
        if (category == null || category.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is required.");

        // uniqueness check
        courseRepository.findByCourseCodeIgnoreCase(code)
            .ifPresent(c -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Course code already exists."); });

        Program program = programRepository.findById(req.getProgramId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found."));

        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setDescription(req.getDescription());
        course.setModality(req.getModality());
        course.setCategory(category);
        course.setRequiresPrereq(Boolean.TRUE.equals(req.isRequiresPrereq()));
        course.setProgram(program);

        return courseRepository.save(course);
    }

    @Transactional
    public Course update(Long courseId, CourseRequest req) {
        Course existing = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));

        String nextCode = req.getCourseCode() != null ? req.getCourseCode().trim() : null;
        String nextName = req.getCourseName() != null ? req.getCourseName().trim() : null;
        String nextCategory = req.getCategory() != null ? req.getCategory().trim() : null;

        if (nextCode == null || nextCode.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course code is required.");
        if (nextName == null || nextName.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course name is required.");
        if (req.getProgramId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program is required.");
        if (nextCategory == null || nextCategory.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is required.");

        // if course code changed, check uniqueness
        if (!existing.getCourseCode().equalsIgnoreCase(nextCode)) {
            courseRepository.findByCourseCodeIgnoreCase(nextCode)
                .ifPresent(c -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Course code already exists."); });
        }

        Program program = programRepository.findById(req.getProgramId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found."));

        existing.setCourseCode(nextCode);
        existing.setCourseName(nextName);
        existing.setDescription(req.getDescription());
        existing.setModality(req.getModality());
        existing.setCategory(nextCategory);
        existing.setRequiresPrereq(Boolean.TRUE.equals(req.isRequiresPrereq()));
        existing.setProgram(program);

        return courseRepository.save(existing);
    }
    
    @Transactional
    public void deleteCascade(Long courseId) {
        Course existing = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));

        // Delete prerequisite rows that reference this course in either column
        prerequisiteRepository.deleteByCourse_CourseId(courseId);
        prerequisiteRepository.deleteByPrerequisiteCourse_CourseId(courseId);

        courseRepository.delete(existing);
    }
}