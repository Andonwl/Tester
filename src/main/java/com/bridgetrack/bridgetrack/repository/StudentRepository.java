package com.bridgetrack.bridgetrack.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

	boolean existsByStudentEmailIgnoreCase(String studentEmail);
	Optional<Student> findByStudentEmailIgnoreCase(String studentEmail);

	boolean existsByPersonalEmailIgnoreCase(String personalEmail);
	Optional<Student> findByPersonalEmailIgnoreCase(String personalEmail);
	
	long countByStatusIgnoreCase(String status);
	
	Optional<Student> findByStudentEmail(String studentEmail);
	
	

}
