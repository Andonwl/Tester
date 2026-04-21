package com.bridgetrack.bridgetrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bridgetrack.bridgetrack.model.AttendanceRecord;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByAttendanceSessionAttendanceSessionId(Long attendanceSessionId);

    Optional<AttendanceRecord> findByAttendanceSessionAttendanceSessionIdAndStudentStudentId(
            Long attendanceSessionId,
            Long studentId
    );
}