package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.dto.CreatePrerequisiteRequest;
import com.bridgetrack.bridgetrack.model.Prerequisite;
import com.bridgetrack.bridgetrack.service.PrerequisiteService;

@RestController
@RequestMapping("/prerequisites")
public class PrerequisiteController {

    private final PrerequisiteService prerequisiteService;

    public PrerequisiteController(PrerequisiteService prerequisiteService) {
        this.prerequisiteService = prerequisiteService;
    }

    
    @PostMapping
    public Prerequisite create(@RequestBody CreatePrerequisiteRequest req) {
        return prerequisiteService.addPrerequisite(req.getCourseId(), req.getPrerequisiteCourseId());
    }

    
    @GetMapping("/course/{courseId}")
    public List<Prerequisite> listForCourse(@PathVariable Long courseId) {
        return prerequisiteService.listForCourse(courseId);
    }

  
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        prerequisiteService.delete(id);
    }
}
