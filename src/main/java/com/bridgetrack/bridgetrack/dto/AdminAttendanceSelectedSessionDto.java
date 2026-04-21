package com.bridgetrack.bridgetrack.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminAttendanceSelectedSessionDto {

    private Long attendanceSessionId;
    private String instructorName;
    private String courseCode;
    private String sectionLabel;
    private String sessionDate;
    private List<AttendancePreviewStudentDto> attendanceRecords = new ArrayList<>();

    public Long getAttendanceSessionId() {
        return attendanceSessionId;
    }

    public void setAttendanceSessionId(Long attendanceSessionId) {
        this.attendanceSessionId = attendanceSessionId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSectionLabel() {
        return sectionLabel;
    }

    public void setSectionLabel(String sectionLabel) {
        this.sectionLabel = sectionLabel;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public List<AttendancePreviewStudentDto> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void setAttendanceRecords(List<AttendancePreviewStudentDto> attendanceRecords) {
        this.attendanceRecords = attendanceRecords;
    }
}