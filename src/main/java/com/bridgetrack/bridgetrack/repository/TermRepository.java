package com.bridgetrack.bridgetrack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.Term;

public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByTermNameIgnoreCase(String termName);
    Optional<Term> findByTermId(Long termId);
}
