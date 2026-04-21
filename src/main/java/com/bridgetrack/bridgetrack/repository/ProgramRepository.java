package com.bridgetrack.bridgetrack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    Optional<Program> findByProgramNameIgnoreCase(String programName);
}