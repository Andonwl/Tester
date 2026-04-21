package com.bridgetrack.bridgetrack.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgetrack.bridgetrack.dto.AttendancePageData;
import com.bridgetrack.bridgetrack.dto.AttendanceSaveRequest;
import com.bridgetrack.bridgetrack.dto.InstructorRosterStudentDto;
import com.bridgetrack.bridgetrack.dto.InstructorSectionOptionDto;
import com.bridgetrack.bridgetrack.model.AttendanceRecord;
import com.bridgetrack.bridgetrack.model.AttendanceSessions;
import com.bridgetrack.bridgetrack.model.AttendanceStatus;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.model.Student;
import com.bridgetrack.bridgetrack.repository.AttendanceRecordRepository;
import com.bridgetrack.bridgetrack.repository.AttendanceSessionsRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;
import com.bridgetrack.bridgetrack.repository.StudentRepository;

@Service
public class InstructorAttendanceService {

    private final InstructorRosterService instructorRosterService;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;
    private final AttendanceSessionsRepository attendanceSessionsRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public InstructorAttendanceService(InstructorRosterService instructorRosterService,
                                       SectionRepository sectionRepository,
                                       StudentRepository studentRepository,
                                       AttendanceSessionsRepository attendanceSessionsRepository,
                                       AttendanceRecordRepository attendanceRecordRepository) {
        this.instructorRosterService = instructorRosterService;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
        this.attendanceSessionsRepository = attendanceSessionsRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Transactional(readOnly = true)
    public AttendancePageData loadAttendancePage(String instructorEmail, Long sectionId, LocalDate sessionDate) {
        AttendancePageData data = new AttendancePageData();

        Long instructorId = instructorRosterService.getLoggedInInstructorId(instructorEmail);
        List<InstructorSectionOptionDto> sectionOptions = instructorRosterService.getInstructorSections(instructorId);

        data.setSectionOptions(sectionOptions);
        data.setSelectedSectionId(sectionId);
        data.setSessionDate(sessionDate != null ? sessionDate.toString() : null);
        data.setPastSession(sessionDate != null && sessionDate.isBefore(LocalDate.now()));

        if (sectionId == null || sessionDate == null) {
            return data;
        }

        if (sessionDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Future attendance entries are not allowed.");
        }

        InstructorSectionOptionDto selectedSection =
                instructorRosterService.getSectionOption(instructorId, sectionId);

        List<InstructorRosterStudentDto> rosterStudents =
                instructorRosterService.getRosterForSection(instructorId, sectionId);

        Map<Long, String> attendanceMap = new LinkedHashMap<>();

        Optional<AttendanceSessions> existingSession =
                attendanceSessionsRepository.findBySectionSectionIdAndSessionDate(sectionId, sessionDate);

        if (existingSession.isPresent()) {
            List<AttendanceRecord> existingRecords =
                    attendanceRecordRepository.findByAttendanceSessionAttendanceSessionId(
                            existingSession.get().getAttendanceSessionId()
                    );

            for (AttendanceRecord record : existingRecords) {
                if (record.getStudent() != null && record.getStatus() != null) {
                    attendanceMap.put(record.getStudent().getStudentId(), record.getStatus().name());
                }
            }
        }

        data.setSelectedSection(selectedSection);
        data.setRosterStudents(rosterStudents);
        data.setAttendanceMap(attendanceMap);

        return data;
    }

    @Transactional
    public void saveAttendance(String instructorEmail, AttendanceSaveRequest request) {
        if (request.getSectionId() == null) {
            throw new IllegalArgumentException("Section is required.");
        }

        if (request.getSessionDate() == null) {
            throw new IllegalArgumentException("Session date is required.");
        }

        if (request.getSessionDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Future attendance entries are not allowed.");
        }

        Long instructorId = instructorRosterService.getLoggedInInstructorId(instructorEmail);

        InstructorSectionOptionDto selectedSection =
                instructorRosterService.getSectionOption(instructorId, request.getSectionId());

        if (selectedSection == null) {
            throw new IllegalArgumentException("You do not have access to that section.");
        }

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new IllegalArgumentException("Section not found."));

        AttendanceSessions session = attendanceSessionsRepository
                .findBySectionSectionIdAndSessionDate(request.getSectionId(), request.getSessionDate())
                .orElseGet(() -> {
                    AttendanceSessions newSession = new AttendanceSessions();
                    newSession.setSection(section);
                    newSession.setSessionDate(request.getSessionDate());
                    return attendanceSessionsRepository.save(newSession);
                });

        List<InstructorRosterStudentDto> rosterStudents =
                instructorRosterService.getRosterForSection(instructorId, request.getSectionId());

        Map<Long, InstructorRosterStudentDto> allowedStudents = new LinkedHashMap<>();
        for (InstructorRosterStudentDto student : rosterStudents) {
            allowedStudents.put(student.getStudentId(), student);
        }

        for (Map.Entry<Long, AttendanceStatus> entry : request.getStudentStatuses().entrySet()) {
            Long studentId = entry.getKey();
            AttendanceStatus status = entry.getValue();

            if (!allowedStudents.containsKey(studentId)) {
                throw new IllegalArgumentException("Student " + studentId + " is not in this section.");
            }

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

            AttendanceRecord record = attendanceRecordRepository
                    .findByAttendanceSessionAttendanceSessionIdAndStudentStudentId(
                            session.getAttendanceSessionId(),
                            studentId
                    )
                    .orElseGet(() -> {
                        AttendanceRecord newRecord = new AttendanceRecord();
                        newRecord.setAttendanceSession(session);
                        newRecord.setStudent(student);
                        return newRecord;
                    });

            record.setStatus(status);
            attendanceRecordRepository.save(record);
        }
    }
}