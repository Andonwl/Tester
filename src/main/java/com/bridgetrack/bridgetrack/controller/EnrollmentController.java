package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bridgetrack.bridgetrack.dto.EnrollSelfRequest;
import com.bridgetrack.bridgetrack.dto.EnrollmentRequest;
import com.bridgetrack.bridgetrack.dto.UpdateEnrollmentStatusRequest;
import com.bridgetrack.bridgetrack.model.Enrollment;
import com.bridgetrack.bridgetrack.model.EnrollmentStatus;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.EnrollmentRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;
import com.bridgetrack.bridgetrack.service.EnrollmentService;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

   private final EnrollmentService enrollmentService;
   
   public EnrollmentController(EnrollmentService enrollmentService) {
	   this.enrollmentService = enrollmentService;
   }
   
   @PostMapping
   public Enrollment enrollStudent (@RequestBody EnrollmentRequest request) {
	   return enrollmentService.enrollStudent(
			   request.getStudentId(),
			   request.getSectionId()
			   );
   }
   
   @PostMapping("/self")
   public Enrollment enrollSelf(@RequestBody EnrollSelfRequest request) {
   		return enrollmentService.enrollSelf(request.getSectionId());
 }
   
   @PatchMapping("/{id}/status")
   public Enrollment updateStatus(
           @PathVariable Long id,
           @RequestBody UpdateEnrollmentStatusRequest request
   ) {
       return enrollmentService.updateStatus(id, request.getStatus());
   }
   
   @GetMapping("/my")
   public List<Enrollment> myEnrollments() {
	   return enrollmentService.getMyEnrollments();
   }
   
   }

