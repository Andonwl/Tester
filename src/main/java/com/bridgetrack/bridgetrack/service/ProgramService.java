package com.bridgetrack.bridgetrack.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bridgetrack.bridgetrack.model.Program;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.ProgramRepository;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;

    public ProgramService(ProgramRepository programRepository, CourseRepository courseRepository) {
        this.programRepository = programRepository;
        this.courseRepository = courseRepository;
    }

    public Program create(Program program) {
        if (program == null || program.getProgramName() == null || program.getProgramName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program name is required.");
        }

        programRepository.findByProgramNameIgnoreCase(program.getProgramName().trim())
                .ifPresent(p -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Program already exists.");
                });

        program.setProgramName(program.getProgramName().trim());
        return programRepository.save(program);
    }

    public List<Program> getAll() {
        return programRepository.findAll();
    }

    public Program getById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found."));
    }

    @Transactional
    public Program update(Long id, Program incoming) {
        Program existing = programRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found."));

        String nextName = incoming != null ? incoming.getProgramName() : null;
        if (nextName == null || nextName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Program name is required.");
        }
        nextName = nextName.trim();

        // Only check duplicates if name actually changes
        if (!existing.getProgramName().equalsIgnoreCase(nextName)) {
            programRepository.findByProgramNameIgnoreCase(nextName)
                    .ifPresent(p -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Program already exists.");
                    });
        }

        existing.setProgramName(nextName);
        return programRepository.save(existing);
    }

    @Transactional
    public void deleteCascade(Long id) {
        Program existing = programRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found."));

        // Delete dependent courses first
        courseRepository.deleteByProgram_ProgramId(id);

        // Now delete the program
        programRepository.delete(existing);
    }
}