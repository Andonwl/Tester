package com.bridgetrack.bridgetrack.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgetrack.bridgetrack.exception.InvalidPrerequisiteException;
import com.bridgetrack.bridgetrack.exception.PrerequisiteAlreadyExistsException;
import com.bridgetrack.bridgetrack.exception.ResourceNotFoundException;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Prerequisite;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.PrerequisiteRepository;

@Service
@Transactional
public class PrerequisiteService {

    private final PrerequisiteRepository prerequisiteRepository;
    private final CourseRepository courseRepository;

    public PrerequisiteService(PrerequisiteRepository prerequisiteRepository,
                               CourseRepository courseRepository) {
        this.prerequisiteRepository = prerequisiteRepository;
        this.courseRepository = courseRepository;
    }

    public Prerequisite addPrerequisite(Long courseId, Long prerequisiteCourseId) {

        
        if (courseId == null || prerequisiteCourseId == null) {
            throw new InvalidPrerequisiteException("courseId and prerequisiteCourseId are required.");
        }

        if (courseId.equals(prerequisiteCourseId)) {
            throw new InvalidPrerequisiteException("A course cannot be a prerequisite of itself.");
        }

        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        Course prereq = courseRepository.findById(prerequisiteCourseId)
                .orElseThrow(() -> new ResourceNotFoundException("Prerequisite course not found: " + prerequisiteCourseId));

        
        prerequisiteRepository
                .findByCourse_CourseIdAndPrerequisiteCourse_CourseId(courseId, prerequisiteCourseId)
                .ifPresent(p -> { throw new PrerequisiteAlreadyExistsException("Prerequisite already exists."); });

        
        if (createsCycle(courseId, prerequisiteCourseId, new HashSet<>())) {
            throw new InvalidPrerequisiteException("Circular prerequisite detected. This link would create a cycle.");
        }

        Prerequisite p = new Prerequisite();
        p.setCourse(course);
        p.setPrerequisiteCourse(prereq);

        return prerequisiteRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Prerequisite> listForCourse(Long courseId) {
        if (courseId == null) {
            throw new InvalidPrerequisiteException("courseId is required.");
        }
        return prerequisiteRepository.findByCourse_CourseId(courseId);
    }

    public void delete(Long prerequisiteId) {
        if (prerequisiteId == null) {
            throw new InvalidPrerequisiteException("prerequisiteId is required.");
        }

        
        boolean exists = prerequisiteRepository.existsById(prerequisiteId);
        if (!exists) {
            throw new ResourceNotFoundException("Prerequisite not found: " + prerequisiteId);
        }

        prerequisiteRepository.deleteById(prerequisiteId);
    }

  
    private boolean createsCycle(Long courseId, Long prerequisiteCourseId, Set<Long> visitedCourseIds) {

        
        if (!visitedCourseIds.add(prerequisiteCourseId)) {
            return false;
        }

        
        List<Prerequisite> prereqsOfPrereq =
                prerequisiteRepository.findByCourse_CourseId(prerequisiteCourseId);

        for (Prerequisite p : prereqsOfPrereq) {
            Long next = p.getPrerequisiteCourse().getCourseId();

            
            if (courseId.equals(next)) {
                return true;
            }

            if (createsCycle(courseId, next, visitedCourseIds)) {
                return true;
            }
        }

        return false;
    }
}

