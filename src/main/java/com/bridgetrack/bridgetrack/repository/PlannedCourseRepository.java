package com.bridgetrack.bridgetrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.PlannedCourse;

public interface PlannedCourseRepository extends JpaRepository<PlannedCourse, Long> {

    List<PlannedCourse> findByStudentStudentIdOrderBySemesterLabelAscTermNumberAsc(Long studentId);

    List<PlannedCourse> findByStudentStudentIdAndEnrolledFalseOrderBySemesterLabelAscTermNumberAsc(Long studentId);

    List<PlannedCourse> findByStudentStudentIdAndEnrolledTrueOrderBySemesterLabelAscTermNumberAsc(Long studentId);

    // Existing method you had (kept)
    List<PlannedCourse> findByStudentStudentIdAndEnrolledTrueOrderBySemesterLabelAscTermNumberAscPlannedCourseIdAsc(Long studentId);

    boolean existsByStudentStudentIdAndCourseCourseIdAndSemesterLabelAndTermNumber(
            Long studentId,
            Long courseId,
            String semesterLabel,
            Integer termNumber
    );

    Optional<PlannedCourse> findByStudentStudentIdAndCourseCourseIdAndSemesterLabelAndTermNumber(
            Long studentId,
            Long courseId,
            String semesterLabel,
            Integer termNumber
    );

    Optional<PlannedCourse> findByPlannedCourseIdAndStudentStudentId(Long plannedCourseId, Long studentId);

    // NEW: used when dropping from My Schedule to flip the planned course back to enrolled=false
    Optional<PlannedCourse> findFirstByStudentStudentIdAndCourseCourseIdAndEnrolledTrueOrderBySemesterLabelDescTermNumberDesc(
            Long studentId,
            Long courseId
    );
}