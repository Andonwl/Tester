package com.bridgetrack.bridgetrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.Prerequisite;

public interface PrerequisiteRepository extends JpaRepository<Prerequisite, Long> {

    List<Prerequisite> findByCourse_CourseId(Long courseId);

    Optional<Prerequisite> findByCourse_CourseIdAndPrerequisiteCourse_CourseId(Long courseId, Long prerequisiteCourseId);
    
    void deleteByCourse_CourseId(Long courseId);
    void deleteByPrerequisiteCourse_CourseId(Long courseId);
}

