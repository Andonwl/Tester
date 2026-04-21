package com.bridgetrack.bridgetrack.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.repository.SectionRepository;

@Service
public class AdminAvailableSectionService {

    private final SectionRepository sectionRepository;
    private final AdminEnrollmentValidationService adminEnrollmentValidationService;

    public AdminAvailableSectionService(
            SectionRepository sectionRepository,
            AdminEnrollmentValidationService adminEnrollmentValidationService
    ) {
        this.sectionRepository = sectionRepository;
        this.adminEnrollmentValidationService = adminEnrollmentValidationService;
    }

    public List<Section> findAvailableSectionsForStudent(
            Long studentId,
            Long termId,
            String modality,
            String query
    ) {
        // First apply the admin’s filter criteria
        List<Section> filtered = sectionRepository.findForRegistration(termId, modality, query);

        // If no student is selected, nothing is “available” yet (prevents misleading list)
        if (studentId == null) {
            return List.of();
        }

        // Then apply eligibility rules (already enrolled, conflicts, prereqs)
        List<Section> available = new ArrayList<>();
        for (Section s : filtered) {
            try {
                adminEnrollmentValidationService.validateCanEnroll(studentId, s);
                available.add(s);
            } catch (RuntimeException ignored) {
                // Not eligible => do not show
            }
        }

        return available;
    }
}