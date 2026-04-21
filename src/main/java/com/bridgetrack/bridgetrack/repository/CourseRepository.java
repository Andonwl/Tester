package com.bridgetrack.bridgetrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(String courseCode);

    Optional<Course> findByCourseCodeIgnoreCase(String courseCode);

    List<Course> findAllByOrderByCourseCodeAsc();

    List<Course> findByProgram_ProgramIdOrderByCourseCodeAsc(Long programId);

    void deleteByProgram_ProgramId(Long programId);
}