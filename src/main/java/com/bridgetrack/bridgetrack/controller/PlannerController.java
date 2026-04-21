package com.bridgetrack.bridgetrack.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import com.bridgetrack.bridgetrack.dto.PlannerEnrollmentRequestDto;
import com.bridgetrack.bridgetrack.dto.PlannerPageDto;
import com.bridgetrack.bridgetrack.service.PlannerService;

@RestController
@RequestMapping("/api/planner")
public class PlannerController {

    private final PlannerService plannerService;

    public PlannerController(PlannerService plannerService) {
        this.plannerService = plannerService;
    }

    @GetMapping
    public PlannerPageDto getPlannerPage(Authentication authentication) {
        String username = authentication.getName();
        return plannerService.getPlannerPage(username);
    }

    @PostMapping("/enroll")
    public ResponseEntity<String> enroll(
            Authentication authentication,
            @RequestBody PlannerEnrollmentRequestDto request) {
        String username = authentication.getName();
        plannerService.addEnrollmentForLoggedInStudent(username, request);
        return ResponseEntity.ok("Enrollment saved.");
    }

    @DeleteMapping("/enroll/{sectionId}")
    public ResponseEntity<String> removeEnrollment(
            Authentication authentication,
            @PathVariable Long sectionId) {
        String username = authentication.getName();
        plannerService.removeEnrollmentForLoggedInStudent(username, sectionId);
        return ResponseEntity.ok("Enrollment removed.");
    }

    @DeleteMapping("/reset")
    public ResponseEntity<String> resetPlanner(Authentication authentication) {
        String username = authentication.getName();
        plannerService.resetPlanner(username);
        return ResponseEntity.ok("Planner reset.");
    }
}
