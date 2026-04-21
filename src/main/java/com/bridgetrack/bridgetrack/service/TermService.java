package com.bridgetrack.bridgetrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.model.Term;
import com.bridgetrack.bridgetrack.repository.TermRepository;

@Service
public class TermService {

    private final TermRepository termRepository;

    public TermService(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    public Term create(Term term) {
        termRepository.findByTermNameIgnoreCase(term.getTermName())
                .ifPresent(t -> { throw new RuntimeException("Term already exists (name)."); });

        term.setTermId(null);

        return termRepository.save(term);
    }

    public List<Term> getAll() {
        return termRepository.findAll();
    }

    public Term getById(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Term not found."));
    }

    public Term update(Long id, Term incoming) {
        Term existing = getById(id);

        String incomingName = incoming.getTermName() == null ? "" : incoming.getTermName().trim();
        if (incomingName.isEmpty()) {
            throw new RuntimeException("Term name is required.");
        }

        String existingName = existing.getTermName() == null ? "" : existing.getTermName().trim();
        boolean nameChanged = !incomingName.equalsIgnoreCase(existingName);

        if (nameChanged) {
            termRepository.findByTermNameIgnoreCase(incomingName)
                    .ifPresent(t -> { throw new RuntimeException("Term already exists (name)."); });
        }

        existing.setTermName(incomingName);
        existing.setStartDate(incoming.getStartDate());
        existing.setEndDate(incoming.getEndDate());

        return termRepository.save(existing);
    }

    public void delete(Long id) {
        Term existing = getById(id);
        termRepository.delete(existing);
    }
}