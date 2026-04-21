package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.model.Term;
import com.bridgetrack.bridgetrack.service.TermService;

@RestController
@RequestMapping("/terms")
public class TermController {

    private final TermService termService;

    public TermController(TermService termService) {
        this.termService = termService;
    }

    @PostMapping
    public Term create(@RequestBody Term term) {
        return termService.create(term);
    }

    @GetMapping
    public List<Term> all() {
        return termService.getAll();
    }

    @GetMapping("/{id}")
    public Term byId(@PathVariable Long id) {
        return termService.getById(id);
    }

    @PutMapping("/{id}")
    public Term update(@PathVariable Long id, @RequestBody Term term) {
        return termService.update(id, term);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        termService.delete(id);
    }
}