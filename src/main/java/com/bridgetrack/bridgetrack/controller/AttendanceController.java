package com.bridgetrack.bridgetrack.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.dto.CreateAttendanceRecordRequest;
import com.bridgetrack.bridgetrack.dto.CreateAttendanceSessionRequest;
import com.bridgetrack.bridgetrack.model.AttendanceRecord;
import com.bridgetrack.bridgetrack.model.AttendanceSessions;
import com.bridgetrack.bridgetrack.model.AttendanceStatus;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.AttendanceRecordRepository;
import com.bridgetrack.bridgetrack.repository.AttendanceSessionsRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceSessionsRepository attendanceSessionsRepository;
    private final AttendanceRecordRepository attendanceRecordsRepository;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;

    public AttendanceController(
            AttendanceSessionsRepository attendanceSessionsRepository,
            AttendanceRecordRepository attendanceRecordsRepository,
            SectionRepository sectionRepository,
            StudentRepository studentRepository
    ) {
        this.attendanceSessionsRepository = attendanceSessionsRepository;
        this.attendanceRecordsRepository = attendanceRecordsRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/sessions")
    public ResponseEntity<?> createSession(@RequestBody CreateAttendanceSessionRequest request) {
        if (request.getSectionId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Section ID is required"));
        }

        if (request.getSessionDate() == null || request.getSessionDate().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Session date is required"));
        }

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));

        AttendanceSessions session = new AttendanceSessions();
        session.setSection(section);
        session.setSessionDate(LocalDate.parse(request.getSessionDate()));
        session.setCreatedBy(request.getCreatedBy());

        AttendanceSessions saved = attendanceSessionsRepository.save(session);

        return ResponseEntity.ok(Map.of(
                "id", saved.getAttendanceSessionId(),
                "message", "Attendance session created"
        ));
    }

    @PostMapping("/records")
    public ResponseEntity<?> createRecord(@RequestBody CreateAttendanceRecordRequest request) {
        if (request.getAttendanceSessionId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Attendance session ID is required"));
        }

        if (request.getStudentId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Student ID is required"));
        }

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Attendance status is required"));
        }

        AttendanceSessions session = attendanceSessionsRepository.findById(request.getAttendanceSessionId())
                .orElseThrow(() -> new RuntimeException("Attendance session not found"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        AttendanceRecord record = new AttendanceRecord();
        record.setAttendanceSession(session);
        record.setStudent(student);
        record.setStatus(AttendanceStatus.valueOf(request.getStatus().trim().toUpperCase()));

        attendanceRecordsRepository.save(record);

        return ResponseEntity.ok(Map.of("message", "Attendance record saved"));
    }
}