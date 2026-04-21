package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.dto.StudentRegistrationRequest;
import com.bridgetrack.bridgetrack.dto.StudentRegistrationResult;
import com.bridgetrack.bridgetrack.dto.StudentUpdateRequest;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.service.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public StudentRegistrationResult create(@RequestBody StudentRegistrationRequest request) {
        return studentService.registerStudent(request);
    }

    @GetMapping
    public List<Student> list() {
        return studentService.getAllStudents();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id,
                                                 @RequestBody StudentUpdateRequest request) {
        Student updatedStudent = studentService.updateStudent(id, request);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}