package com.bridgetrack.bridgetrack.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.exception.PrerequisiteNotMetException;
import com.bridgetrack.bridgetrack.exception.ResourceNotFoundException;
import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.EnrollmentStatus;
import com.bridgetrack.bridgetrack.model.Prerequisite;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.PrerequisiteRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;
import com.bridgetrack.bridgetrack.security.SecurityUtil;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             SectionRepository sectionRepository,
                             PrerequisiteRepository prerequisiteRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    public Enrollment enrollStudent(Long studentId, Long sectionId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + sectionId));

        
        checkPrerequisites(studentId, section.getCourse().getCourseId());

        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setSection(section);
        e.setStatus(EnrollmentStatus.ENROLLED);

        return enrollmentRepository.save(e);
    }
    
    public Enrollment updateStatus(Long enrollmentId, EnrollmentStatus newStatus) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));

        if (newStatus == null) {
            throw new IllegalArgumentException("Status is required");
        }

        enrollment.setStatus(newStatus);
        return enrollmentRepository.save(enrollment);
    }
    
    public List<Enrollment> getMyEnrollments() {
        String studentEmail = SecurityUtil.currentEmail();

        Student student = studentRepository.findByStudentEmailIgnoreCase(studentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found for user: " + studentEmail));

        return enrollmentRepository.findByStudentStudentId(student.getStudentId());
    }

    public Enrollment enrollSelf(Long sectionId) {

        String email = SecurityUtil.currentEmail();

        Student student = studentRepository.findByStudentEmailIgnoreCase(email)
            .orElseThrow(() ->
                new ResourceNotFoundException("Student not found for user: " + email)
            );

        
        return enrollStudent(student.getStudentId(), sectionId);
    }
    
    private void checkPrerequisites(Long studentId, Long courseId) {

        
        List<Prerequisite> prereqs = prerequisiteRepository.findByCourse_CourseId(courseId);
        if (prereqs == null || prereqs.isEmpty()) return;

        
        List<Enrollment> enrollments = enrollmentRepository.findByStudentStudentId(studentId);

       
        Set<Long> completedCourseIds = enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .map(e -> e.getSection().getCourse().getCourseId())
                .collect(Collectors.toSet());

        
        List<Long> missing = prereqs.stream()
                .map(p -> p.getPrerequisiteCourse().getCourseId())
                .filter(reqCourseId -> !completedCourseIds.contains(reqCourseId))
                .distinct()
                .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            throw new PrerequisiteNotMetException(
                    "Missing prerequisite course(s) for courseId=" + courseId + ": " + missing
            );
        }
    }
}