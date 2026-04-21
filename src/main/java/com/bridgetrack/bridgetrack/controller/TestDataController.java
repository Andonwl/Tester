package com.bridgetrack.bridgetrack.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TestDataController {
	
	
	@GetMapping("/api/instructor/courses")
	public ResponseEntity<?> getCourses() {
	    return ResponseEntity.ok(List.of(
	            Map.of("id", 1, "name", "Advanced Java"),
	            Map.of("id", 2, "name", "Website Design")
	    ));
	}

	@GetMapping("/api/instructor/courses/{courseId}/sections")
	public ResponseEntity<?> getSections(@PathVariable Long courseId) {
	    return ResponseEntity.ok(List.of(
	            Map.of("id", 10, "name", "Section A"),
	            Map.of("id", 11, "name", "Section B")
	    ));
	}

	@GetMapping("/api/sections/{sectionId}/students")
	public ResponseEntity<?> getStudents(@PathVariable Long sectionId) {
	    return ResponseEntity.ok(List.of(
	            Map.of("id", 100, "firstName", "Jane", "lastName", "Doe"),
	            Map.of("id", 101, "firstName", "John", "lastName", "Doe")
	    ));
	}

	/*@PostMapping("/api/attendance/sessions")
	public ResponseEntity<?> createSession(@RequestBody Map<String,Object> body) {
	    return ResponseEntity.ok(Map.of("id", 999));
	}

	@PostMapping("/api/attendance/records")
	public ResponseEntity<?> createRecord(@RequestBody Map<String,Object> body) {
	    return ResponseEntity.ok(Map.of("message","Attendance saved"));
	}*/

	@GetMapping("/api/attendance/history")
	public ResponseEntity<?> getHistory() {
	    return ResponseEntity.ok(List.of(
	            Map.of(
	                    "sessionDate","2026-03-08",
	                    "courseName","Advanced Java",
	                    "sectionName","Section A",
	                    "studentName","Jane Doe",
	                    "status","PRESENT"
	            ),
	            Map.of(
	                    "sessionDate","2026-03-08",
	                    "courseName","Website Design",
	                    "sectionName","Section B",
	                    "studentName","John Doe",
	                    "status","ABSENT"
	            )
	    ));
	}
	}