package com.bridgetrack.bridgetrack.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgetrack.bridgetrack.dto.AttendanceHistoryRecordDto;
import com.bridgetrack.bridgetrack.dto.AttendanceHistorySessionDto;
import com.bridgetrack.bridgetrack.dto.InstructorSectionOptionDto;
import com.bridgetrack.bridgetrack.model.AttendanceRecord;
import com.bridgetrack.bridgetrack.model.AttendanceSessions;
import com.bridgetrack.bridgetrack.repository.AttendanceRecordRepository;
import com.bridgetrack.bridgetrack.repository.AttendanceSessionsRepository;

@Service
public class InstructorAttendanceHistoryService {

    private final InstructorRosterService instructorRosterService;
    private final AttendanceSessionsRepository attendanceSessionsRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public InstructorAttendanceHistoryService(InstructorRosterService instructorRosterService,
                                              AttendanceSessionsRepository attendanceSessionsRepository,
                                              AttendanceRecordRepository attendanceRecordRepository) {
        this.instructorRosterService = instructorRosterService;
        this.attendanceSessionsRepository = attendanceSessionsRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Transactional(readOnly = true)
    public List<AttendanceHistorySessionDto> getAttendanceHistoryForInstructor(String instructorEmail) {
        Long instructorId = instructorRosterService.getLoggedInInstructorId(instructorEmail);
        List<InstructorSectionOptionDto> instructorSections =
                instructorRosterService.getInstructorSections(instructorId);

        List<AttendanceHistorySessionDto> history = new ArrayList<>();

        for (InstructorSectionOptionDto sectionOption : instructorSections) {
            List<AttendanceSessions> sessions =
                    attendanceSessionsRepository.findBySectionSectionIdOrderBySessionDateDesc(
                            sectionOption.getSectionId()
                    );

            for (AttendanceSessions session : sessions) {
                AttendanceHistorySessionDto sessionDto = new AttendanceHistorySessionDto();
                sessionDto.setAttendanceSessionId(session.getAttendanceSessionId());
                sessionDto.setSectionName(sectionOption.getOptionLabel());
                sessionDto.setSessionDate(
                        session.getSessionDate() != null ? session.getSessionDate().toString() : ""
                );

                List<AttendanceRecord> records =
                        attendanceRecordRepository.findByAttendanceSessionAttendanceSessionId(
                                session.getAttendanceSessionId()
                        );

                List<AttendanceHistoryRecordDto> recordDtos = new ArrayList<>();

                for (AttendanceRecord record : records) {
                    AttendanceHistoryRecordDto recordDto = new AttendanceHistoryRecordDto();
                    recordDto.setStudentId(record.getStudent().getStudentId());
                    recordDto.setStudentName(
                            record.getStudent().getFirstName() + " " + record.getStudent().getLastName()
                    );
                    recordDto.setStatus(record.getStatus() != null ? record.getStatus().name() : "");
                    recordDtos.add(recordDto);
                }

                recordDtos.sort(Comparator.comparing(AttendanceHistoryRecordDto::getStudentName));
                sessionDto.setRecords(recordDtos);
                history.add(sessionDto);
            }
        }

        history.sort(Comparator.comparing(AttendanceHistorySessionDto::getSessionDate).reversed());
        return history;
    }
}