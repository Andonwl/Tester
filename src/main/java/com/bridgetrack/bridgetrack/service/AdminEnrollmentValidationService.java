package com.bridgetrack.bridgetrack.service;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.EnrollmentStatus;
import com.bridgetrack.bridgetrack.model.Prerequisite;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.PrerequisiteRepository;

@Service
public class AdminEnrollmentValidationService {

    private final EnrollmentRepository enrollmentRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    public AdminEnrollmentValidationService(
            EnrollmentRepository enrollmentRepository,
            PrerequisiteRepository prerequisiteRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    public void validateCanEnroll(Long studentId, Section candidate) {
        if (studentId == null) throw new IllegalArgumentException("Student is required.");
        if (candidate == null) throw new IllegalArgumentException("Section is required.");
        if (candidate.getSectionId() == null) throw new IllegalArgumentException("Section is required.");
        if (candidate.getCourse() == null || candidate.getCourse().getCourseId() == null) {
            throw new IllegalArgumentException("Section course is missing.");
        }

        List<Enrollment> existing = enrollmentRepository.findByStudentStudentId(studentId);

        // 1) Already enrolled in this exact section (ignore DROPPED)
        boolean alreadyInSection = existing.stream().anyMatch(e ->
                e.getStatus() != EnrollmentStatus.DROPPED
                        && e.getSection() != null
                        && candidate.getSectionId().equals(e.getSection().getSectionId())
        );
        if (alreadyInSection) {
            throw new IllegalStateException("Student is already enrolled in this section.");
        }

        // Optional: block multiple sections of same course in same term (ignore DROPPED)
        boolean sameCourseSameTerm = existing.stream()
                .filter(e -> e.getStatus() != EnrollmentStatus.DROPPED)
                .map(Enrollment::getSection)
                .filter(s -> s != null && s.getCourse() != null && s.getTerm() != null
                        && s.getCourse().getCourseId() != null
                        && s.getTerm().getTermId() != null)
                .anyMatch(s ->
                        s.getCourse().getCourseId().equals(candidate.getCourse().getCourseId())
                                && candidate.getTerm() != null
                                && candidate.getTerm().getTermId() != null
                                && s.getTerm().getTermId().equals(candidate.getTerm().getTermId())
                );
        if (sameCourseSameTerm) {
            throw new IllegalStateException("Student is already enrolled in this course for the selected term.");
        }

        // 2) Schedule conflict
        Enrollment conflict = findScheduleConflict(existing, candidate);
        if (conflict != null) {
            Section other = conflict.getSection();
            String otherCode = (other != null && other.getCourse() != null) ? other.getCourse().getCourseCode() : "another class";
            Long otherSectionId = (other != null) ? other.getSectionId() : null;
            throw new IllegalStateException("Schedule conflict: overlaps with " + otherCode
                    + (otherSectionId != null ? " (Section " + otherSectionId + ")." : "."));
        }

        // 3) Prerequisites
        validatePrereqs(existing, candidate);
    }

    private Enrollment findScheduleConflict(List<Enrollment> existing, Section candidate) {
        if (candidate.getTerm() == null || candidate.getTerm().getTermId() == null) return null;

        // If candidate has no time/day info, allow enrollment
        if (candidate.getStartTime() == null || candidate.getEndTime() == null) return null;
        if (isBlank(candidate.getMeetingDays())) return null;

        for (Enrollment e : existing) {
            // choose what blocks: anything except DROPPED
            if (e.getStatus() == EnrollmentStatus.DROPPED) continue;

            Section other = e.getSection();
            if (other == null || other.getTerm() == null || other.getTerm().getTermId() == null) continue;
            if (!other.getTerm().getTermId().equals(candidate.getTerm().getTermId())) continue;

            if (other.getStartTime() == null || other.getEndTime() == null) continue;
            if (isBlank(other.getMeetingDays())) continue;

            if (!meetingDaysOverlap(candidate.getMeetingDays(), other.getMeetingDays())) continue;

            if (timesOverlap(candidate.getStartTime(), candidate.getEndTime(), other.getStartTime(), other.getEndTime())) {
                return e;
            }
        }
        return null;
    }

    private void validatePrereqs(List<Enrollment> existing, Section candidate) {
        // enforce only if course.requiresPrereq == true
        if (!candidate.getCourse().isRequiresPrereq()) return;

        Long courseId = candidate.getCourse().getCourseId();
        List<Prerequisite> prereqs = prerequisiteRepository.findByCourse_CourseId(courseId);

        if (prereqs.isEmpty()) {
            // course says it requires prereq but none are defined -> allow (change to block if desired)
            return;
        }

        // completed course ids from enrollments
        Set<Long> completedCourseIds = new HashSet<>();
        for (Enrollment e : existing) {
            if (e.getStatus() != EnrollmentStatus.COMPLETED) continue;
            if (e.getSection() == null || e.getSection().getCourse() == null) continue;
            Long completedCourseId = e.getSection().getCourse().getCourseId();
            if (completedCourseId != null) completedCourseIds.add(completedCourseId);
        }

        // check all prereqs satisfied
        List<String> missingCodes = prereqs.stream()
                .map(Prerequisite::getPrerequisiteCourse)
                .filter(c -> c != null && c.getCourseId() != null)
                .filter(c -> !completedCourseIds.contains(c.getCourseId()))
                .map(c -> safe(c.getCourseCode()))
                .distinct()
                .toList();

        if (!missingCodes.isEmpty()) {
            throw new IllegalStateException("Missing prerequisite(s): " + String.join(", ", missingCodes));
        }
    }

    private boolean meetingDaysOverlap(String a, String b) {
        String A = safe(a).toUpperCase();
        String B = safe(b).toUpperCase();
        if (A.isBlank() || B.isBlank()) return false;

        for (int i = 0; i < A.length(); i++) {
            char c = A.charAt(i);
            if (B.indexOf(c) >= 0) return true;
        }
        return false;
    }

    private boolean timesOverlap(LocalTime startA, LocalTime endA, LocalTime startB, LocalTime endB) {
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isBlank();
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}