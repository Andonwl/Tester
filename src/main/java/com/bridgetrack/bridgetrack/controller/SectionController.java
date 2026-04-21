package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.dto.SectionCreateRequest;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Term;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.TermRepository;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionRepository sectionRepo;
    private final CourseRepository courseRepo;
    private final TermRepository termRepo;

    public SectionController(
            SectionRepository sectionRepo,
            CourseRepository courseRepo,
            TermRepository termRepo
    ) {
        this.sectionRepo = sectionRepo;
        this.courseRepo = courseRepo;
        this.termRepo = termRepo;
    }

    @GetMapping
    public List<Section> getAll() {
        return sectionRepo.findAll();
    }

    @PostMapping
    public Section create(@RequestBody SectionCreateRequest req) {
        if (req.getCourseId() == null) {
            throw new RuntimeException("courseId is required.");
        }

        if (req.getTermId() == null) {
            throw new RuntimeException("termId is required.");
        }

        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found: " + req.getCourseId()));

        Term term = termRepo.findById(req.getTermId())
                .orElseThrow(() -> new RuntimeException("Term not found: " + req.getTermId()));

        Section section = new Section();
        section.setCourse(course);
        section.setTerm(term);
        section.setInstructorId(req.getInstructorId());
        section.setMeetingDays(req.getMeetingDays());
        section.setStartTime(req.getStartTime());
        section.setEndTime(req.getEndTime());
        section.setModality(req.getModality());
        section.setCourseRequiresPrereq(
                req.getCourseRequiresPrereq() != null ? req.getCourseRequiresPrereq() : false
        );
        section.setLocation(req.getLocation());
        section.setCapacity(req.getCapacity());
        section.setCourseCategory(
                req.getCourseCategory() != null ? req.getCourseCategory() : ""
        );

        return sectionRepo.save(section);
    }

    @PutMapping("/{sectionId}")
    public Section update(@PathVariable Long sectionId, @RequestBody SectionCreateRequest req) {
        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found: " + sectionId));

        if (req.getCourseId() == null) {
            throw new RuntimeException("courseId is required.");
        }

        if (req.getTermId() == null) {
            throw new RuntimeException("termId is required.");
        }

        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found: " + req.getCourseId()));

        Term term = termRepo.findById(req.getTermId())
                .orElseThrow(() -> new RuntimeException("Term not found: " + req.getTermId()));

        section.setCourse(course);
        section.setTerm(term);
        section.setInstructorId(req.getInstructorId());
        section.setMeetingDays(req.getMeetingDays());
        section.setStartTime(req.getStartTime());
        section.setEndTime(req.getEndTime());
        section.setModality(req.getModality());

        if (req.getCourseRequiresPrereq() != null) {
            section.setCourseRequiresPrereq(req.getCourseRequiresPrereq());
        }

        section.setLocation(req.getLocation());
        section.setCapacity(req.getCapacity());

        if (req.getCourseCategory() != null) {
            section.setCourseCategory(req.getCourseCategory());
        }

        return sectionRepo.save(section);
    }

    @DeleteMapping("/{sectionId}")
    public void delete(@PathVariable Long sectionId) {
        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found: " + sectionId));

        sectionRepo.delete(section);
    }
}