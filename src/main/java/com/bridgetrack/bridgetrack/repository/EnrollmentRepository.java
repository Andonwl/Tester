package com.bridgetrack.bridgetrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bridgetrack.bridgetrack.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentStudentId(Long studentId);

    Optional<Enrollment> findByStudentStudentIdAndSectionSectionId(Long studentId, Long sectionId);

    List<Enrollment> findBySectionSectionId(Long sectionId);

    int deleteByStudentStudentEmailAndSectionSectionId(String studentEmail, Long sectionId);

    Optional<Enrollment> findByStudentStudentEmailAndSectionSectionId(String studentEmail, Long sectionId);

    @Query("""
            SELECT e
            FROM Enrollment e
            LEFT JOIN FETCH e.student s
            LEFT JOIN FETCH e.section sec
            LEFT JOIN FETCH sec.course c
            LEFT JOIN FETCH sec.term t
            WHERE LOWER(s.studentEmail) = LOWER(:studentEmail)
            ORDER BY t.termId, c.courseCode
        """)
    List<Enrollment> findScheduleByStudentStudentEmail(@Param("studentEmail") String studentEmail);
}