package com.bridgetrack.bridgetrack.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.AttendanceSessions;

public interface AttendanceSessionsRepository extends JpaRepository<AttendanceSessions, Long> {

    Optional<AttendanceSessions> findBySectionSectionIdAndSessionDate(Long sectionId, LocalDate sessionDate);
    
    List<AttendanceSessions> findBySectionSectionIdOrderBySessionDateDesc(Long sectionId);
}