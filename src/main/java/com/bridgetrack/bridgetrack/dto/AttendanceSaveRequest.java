package com.bridgetrack.bridgetrack.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bridgetrack.bridgetrack.model.AttendanceStatus;

public class AttendanceSaveRequest {

    private Long sectionId;
    private LocalDate sessionDate;
    private Map<Long, AttendanceStatus> studentStatuses = new LinkedHashMap<>();

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Map<Long, AttendanceStatus> getStudentStatuses() {
        return studentStatuses;
    }

    public void setStudentStatuses(Map<Long, AttendanceStatus> studentStatuses) {
        this.studentStatuses = studentStatuses;
    }
}