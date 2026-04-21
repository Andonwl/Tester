package com.bridgetrack.bridgetrack.dto;

import java.util.ArrayList;
import java.util.List;

public class AttendanceHistorySessionDto {

    private Long attendanceSessionId;
    private String sectionName;
    private String sessionDate;
    private List<AttendanceHistoryRecordDto> records = new ArrayList<>();

    public Long getAttendanceSessionId() {
        return attendanceSessionId;
    }

    public void setAttendanceSessionId(Long attendanceSessionId) {
        this.attendanceSessionId = attendanceSessionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public List<AttendanceHistoryRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<AttendanceHistoryRecordDto> records) {
        this.records = records;
    }
}