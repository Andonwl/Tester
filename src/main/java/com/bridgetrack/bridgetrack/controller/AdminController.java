package com.bridgetrack.bridgetrack.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Controller
public class AdminController {

    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;

    public AdminController(SectionRepository sectionRepository, StudentRepository studentRepository) {
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/admindashboard")
    public String adminPage(Model model) {
        long sectionCount = sectionRepository.count();
        long studentCount = studentRepository.count();

        long activeCount = studentRepository.countByStatusIgnoreCase("ACTIVE");
        long pendingCount = studentRepository.countByStatusIgnoreCase("PENDING");

        long classCount = sectionRepository.countSectionIds();

        List<Student> students = studentRepository.findAll();

        List<Student> duplicateStudents = students.stream()
                .filter(Student::isPossibleDuplicate)
                .collect(Collectors.toList());

        model.addAttribute("sectionCount", sectionCount);
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("students", duplicateStudents);
        model.addAttribute("classCount", classCount);

        return "admindashboard";
    }
}