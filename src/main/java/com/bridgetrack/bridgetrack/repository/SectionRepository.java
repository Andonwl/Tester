package com.bridgetrack.bridgetrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bridgetrack.bridgetrack.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
    long count();

    @Query("SELECT COUNT(s.sectionId) FROM Section s WHERE s.sectionId IS NOT NULL")
    long countSectionIds();

    List<Section> findByCourseCourseId(Long courseId);

    List<Section> findByInstructorIdOrderBySectionIdAsc(Long instructorId);

    /**
     * Filter helper for the Admin Registration page.
     *
     * Works with your entity:
     * - s.term is a ManyToOne Term relationship
     * - s.course is a ManyToOne Course relationship
     */
    @Query("""
        SELECT s
        FROM Section s
        WHERE (:termId IS NULL OR s.term.termId = :termId)
          AND (:modality IS NULL OR :modality = 'ALL' OR LOWER(s.modality) = LOWER(:modality))
          AND (
                :q IS NULL OR :q = '' OR
                LOWER(s.course.courseCode) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(s.course.courseName) LIKE LOWER(CONCAT('%', :q, '%'))
          )
        ORDER BY s.sectionId ASC
    """)
    List<Section> findForRegistration(
            @Param("termId") Long termId,
            @Param("modality") String modality,
            @Param("q") String q
    );
}