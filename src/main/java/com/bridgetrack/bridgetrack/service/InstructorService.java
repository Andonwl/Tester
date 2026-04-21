package com.bridgetrack.bridgetrack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.model.Instructor;
import com.bridgetrack.bridgetrack.repository.InstructorRepository;

@Service
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public Instructor createInstructor(Instructor instructor) {
        if (instructor.getStatus() == null || instructor.getStatus().isBlank()) {
            instructor.setStatus("active");
        }
        return instructorRepository.save(instructor);
    }

    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    public Optional<Instructor> getInstructorById(Long id) {
        return instructorRepository.findById(id);
    }

    public Optional<Instructor> getInstructorByEmail(String email) {
        return instructorRepository.findByEmailIgnoreCase(email);
    }

    public Instructor updateInstructor(Long id, Instructor updatedInstructor) {
        return instructorRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(updatedInstructor.getFirstName());
                    existing.setLastName(updatedInstructor.getLastName());
                    existing.setEmail(updatedInstructor.getEmail());
                    existing.setPhone(updatedInstructor.getPhone());
                    existing.setStatus(updatedInstructor.getStatus());
                    return instructorRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Instructor not found with id: " + id));
    }

    public void deleteInstructor(Long id) {
        instructorRepository.deleteById(id);
    }
}