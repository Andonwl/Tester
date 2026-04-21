package com.bridgetrack.bridgetrack.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AttendancePageData {

    private List<InstructorSectionOptionDto> sectionOptions;
    private InstructorSectionOptionDto selectedSection;
    private Long selectedSectionId;
    private String sessionDate;
    private List<InstructorRosterStudentDto> rosterStudents;
    private Map<Long, String> attendanceMap = new LinkedHashMap<>();
    private boolean pastSession;

    public List<InstructorSectionOptionDto> getSectionOptions() {
        return sectionOptions;
    }

    public void setSectionOptions(List<InstructorSectionOptionDto> sectionOptions) {
        this.sectionOptions = sectionOptions;
    }

    public InstructorSectionOptionDto getSelectedSection() {
        return selectedSection;
    }

    public void setSelectedSection(InstructorSectionOptionDto selectedSection) {
        this.selectedSection = selectedSection;
    }

    public Long getSelectedSectionId() {
        return selectedSectionId;
    }

    public void setSelectedSectionId(Long selectedSectionId) {
        this.selectedSectionId = selectedSectionId;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public List<InstructorRosterStudentDto> getRosterStudents() {
        return rosterStudents;
    }

    public void setRosterStudents(List<InstructorRosterStudentDto> rosterStudents) {
        this.rosterStudents = rosterStudents;
    }

    public Map<Long, String> getAttendanceMap() {
        return attendanceMap;
    }

    public void setAttendanceMap(Map<Long, String> attendanceMap) {
        this.attendanceMap = attendanceMap;
    }

    public boolean isPastSession() {
        return pastSession;
    }

    public void setPastSession(boolean pastSession) {
        this.pastSession = pastSession;
    }
}