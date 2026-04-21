package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.model.Program;
import com.bridgetrack.bridgetrack.service.ProgramService;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @PostMapping
    public Program create(@RequestBody Program program) {
        return programService.create(program);
    }

    @GetMapping
    public List<Program> all() {
        return programService.getAll();
    }

    @GetMapping("/{id}")
    public Program byId(@PathVariable Long id) {
        return programService.getById(id);
    }
    
    @PutMapping("/{id}")
    public Program update(@PathVariable Long id, @RequestBody Program program) {
        return programService.update(id, program);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        programService.deleteCascade(id);
    }
}