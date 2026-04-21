package com.bridgetrack.bridgetrack.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bridgetrack.bridgetrack.dto.AdminAttendanceReportsPageData;
import com.bridgetrack.bridgetrack.dto.AdminAttendanceSelectedSessionDto;
import com.bridgetrack.bridgetrack.dto.AdminAttendanceSessionRowDto;
import com.bridgetrack.bridgetrack.dto.AttendancePreviewStudentDto;
import com.bridgetrack.bridgetrack.dto.AttendanceReportFilterOptionDto;
import com.bridgetrack.bridgetrack.model.AttendanceRecord;
import com.bridgetrack.bridgetrack.model.AttendanceSessions;
import com.bridgetrack.bridgetrack.model.Course;
import com.bridgetrack.bridgetrack.model.Instructor;
import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.repository.AttendanceRecordRepository;
import com.bridgetrack.bridgetrack.repository.AttendanceSessionsRepository;
import com.bridgetrack.bridgetrack.repository.CourseRepository;
import com.bridgetrack.bridgetrack.repository.InstructorRepository;
import com.bridgetrack.bridgetrack.repository.SectionRepository;

@Service
public class AdminAttendanceReportsService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final AttendanceSessionsRepository attendanceSessionsRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public AdminAttendanceReportsService(InstructorRepository instructorRepository,
                                         CourseRepository courseRepository,
                                         SectionRepository sectionRepository,
                                         AttendanceSessionsRepository attendanceSessionsRepository,
                                         AttendanceRecordRepository attendanceRecordRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.attendanceSessionsRepository = attendanceSessionsRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Transactional(readOnly = true)
    public AdminAttendanceReportsPageData loadReportsPage(Long instructorId,
                                                          Long courseId,
                                                          Long sectionId,
                                                          String sessionDate,
                                                          Long attendanceSessionId) {

        AdminAttendanceReportsPageData data = new AdminAttendanceReportsPageData();

        data.setSelectedInstructorId(instructorId);
        data.setSelectedCourseId(courseId);
        data.setSelectedSectionId(sectionId);
        data.setSelectedAttendanceSessionId(attendanceSessionId);
        data.setSelectedSessionDate(sessionDate);

        data.setInstructorOptions(loadInstructorOptions());
        data.setCourseOptions(loadCourseOptions());
        data.setSectionOptions(loadSectionOptions(instructorId, courseId));

        if (instructorId != null) {
            instructorRepository.findById(instructorId).ifPresent(instructor ->
                    data.setSelectedInstructorName(buildInstructorName(instructor)));
        }

        if (courseId != null) {
            courseRepository.findById(courseId).ifPresent(course ->
                    data.setSelectedCourseCode(course.getCourseCode()));
        }

        if (sectionId != null) {
            sectionRepository.findById(sectionId).ifPresent(section ->
                    data.setSelectedSectionLabel(buildSectionLabel(section)));
        }

        List<AdminAttendanceSessionRowDto> rows =
                loadAttendanceSessions(instructorId, courseId, sectionId, sessionDate);

        data.setAttendanceSessions(rows);
        data.setSessionCount(rows.size());

        if (attendanceSessionId != null) {
            data.setSelectedReportSession(loadSelectedSession(attendanceSessionId));
        }

        return data;
    }

    private List<AttendanceReportFilterOptionDto> loadInstructorOptions() {
        List<AttendanceReportFilterOptionDto> options = new ArrayList<>();

        List<Instructor> instructors = instructorRepository.findAll();
        instructors.sort(Comparator.comparing(i -> safe(i.getLastName()) + safe(i.getFirstName())));

        for (Instructor instructor : instructors) {
            options.add(new AttendanceReportFilterOptionDto(
                    instructor.getInstructorId(),
                    buildInstructorName(instructor)
            ));
        }

        return options;
    }

    private List<AttendanceReportFilterOptionDto> loadCourseOptions() {
        List<AttendanceReportFilterOptionDto> options = new ArrayList<>();

        List<Course> courses = courseRepository.findAllByOrderByCourseCodeAsc();
        for (Course course : courses) {
            String label = safe(course.getCourseCode()) + " - " + safe(course.getCourseName());
            options.add(new AttendanceReportFilterOptionDto(course.getCourseId(), label));
        }

        return options;
    }

    private List<AttendanceReportFilterOptionDto> loadSectionOptions(Long instructorId, Long courseId) {
        List<AttendanceReportFilterOptionDto> options = new ArrayList<>();

        List<Section> sections = sectionRepository.findAll();
        sections.sort(Comparator.comparing(Section::getSectionId));

        for (Section section : sections) {
            if (instructorId != null && !instructorId.equals(section.getInstructorId())) {
                continue;
            }

            if (courseId != null) {
                if (section.getCourse() == null || !courseId.equals(section.getCourse().getCourseId())) {
                    continue;
                }
            }

            options.add(new AttendanceReportFilterOptionDto(
                    section.getSectionId(),
                    buildSectionLabel(section)
            ));
        }

        return options;
    }

    private List<AdminAttendanceSessionRowDto> loadAttendanceSessions(Long instructorId,
                                                                      Long courseId,
                                                                      Long sectionId,
                                                                      String sessionDate) {

        LocalDate parsedDate = null;
        if (sessionDate != null && !sessionDate.isBlank()) {
            parsedDate = LocalDate.parse(sessionDate);
        }

        List<AdminAttendanceSessionRowDto> rows = new ArrayList<>();
        List<AttendanceSessions> sessions = attendanceSessionsRepository.findAll();

        sessions.sort(Comparator.comparing(AttendanceSessions::getSessionDate).reversed());

        for (AttendanceSessions attendanceSession : sessions) {
            Section section = attendanceSession.getSection();
            if (section == null) {
                continue;
            }

            if (instructorId != null && !instructorId.equals(section.getInstructorId())) {
                continue;
            }

            if (courseId != null) {
                if (section.getCourse() == null || !courseId.equals(section.getCourse().getCourseId())) {
                    continue;
                }
            }

            if (sectionId != null && !sectionId.equals(section.getSectionId())) {
                continue;
            }

            if (parsedDate != null) {
                if (attendanceSession.getSessionDate() == null || !parsedDate.equals(attendanceSession.getSessionDate())) {
                    continue;
                }
            }

            List<AttendanceRecord> records =
                    attendanceRecordRepository.findByAttendanceSessionAttendanceSessionId(
                            attendanceSession.getAttendanceSessionId()
                    );

            AdminAttendanceSessionRowDto row = new AdminAttendanceSessionRowDto();
            row.setAttendanceSessionId(attendanceSession.getAttendanceSessionId());
            row.setSessionDate(attendanceSession.getSessionDate() != null
                    ? attendanceSession.getSessionDate().toString()
                    : "");
            row.setSectionLabel(buildSectionLabel(section));
            row.setInstructorName(buildInstructorNameFromId(section.getInstructorId()));
            row.setRecordCount(records.size());

            rows.add(row);
        }

        return rows;
    }

    public AdminAttendanceSelectedSessionDto loadSelectedSession(Long attendanceSessionId) {
        AttendanceSessions attendanceSession = attendanceSessionsRepository.findById(attendanceSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Attendance session not found."));

        Section section = attendanceSession.getSection();
        if (section == null) {
            throw new IllegalArgumentException("Attendance session is missing section data.");
        }

        List<AttendanceRecord> records =
                attendanceRecordRepository.findByAttendanceSessionAttendanceSessionId(attendanceSessionId);

        AdminAttendanceSelectedSessionDto dto = new AdminAttendanceSelectedSessionDto();
        dto.setAttendanceSessionId(attendanceSession.getAttendanceSessionId());
        dto.setInstructorName(buildInstructorNameFromId(section.getInstructorId()));
        dto.setCourseCode(section.getCourse() != null ? safe(section.getCourse().getCourseCode()) : "");
        dto.setSectionLabel(buildSectionLabel(section));
        dto.setSessionDate(attendanceSession.getSessionDate() != null
                ? attendanceSession.getSessionDate().toString()
                : "");

        List<AttendancePreviewStudentDto> attendanceRows = new ArrayList<>();

        for (AttendanceRecord record : records) {
            AttendancePreviewStudentDto row = new AttendancePreviewStudentDto();

            if (record.getStudent() != null) {
                String firstName = safe(record.getStudent().getFirstName());
                String lastName = safe(record.getStudent().getLastName());
                row.setStudentName((firstName + " " + lastName).trim());
            } else {
                row.setStudentName("");
            }

            row.setStatus(record.getStatus() != null ? record.getStatus().name() : "");
            attendanceRows.add(row);
        }

        dto.setAttendanceRecords(attendanceRows);

        return dto;
    }
    private String buildSectionLabel(Section section) {
        String courseCode = section.getCourse() != null ? safe(section.getCourse().getCourseCode()) : "";
        String courseName = section.getCourse() != null ? safe(section.getCourse().getCourseName()) : "";
        String termName = "";

        if (section.getTerm() != null) {
        	termName = safe(section.getTerm().getTermName());
        }

        return "Section " + section.getSectionId()
                + " - " + courseCode
                + (courseName.isBlank() ? "" : " - " + courseName)
                + (termName.isBlank() ? "" : " (" + termName + ")");
    }

    private String buildInstructorNameFromId(Long instructorId) {
        if (instructorId == null) {
            return "";
        }

        return instructorRepository.findById(instructorId)
                .map(this::buildInstructorName)
                .orElse("");
    }

    private String buildInstructorName(Instructor instructor) {
        return (safe(instructor.getFirstName()) + " " + safe(instructor.getLastName())).trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}