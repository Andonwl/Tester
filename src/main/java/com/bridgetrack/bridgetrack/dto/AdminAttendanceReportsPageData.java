package com.bridgetrack.bridgetrack.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminAttendanceReportsPageData {

    private List<AttendanceReportFilterOptionDto> instructorOptions = new ArrayList<>();
    private List<AttendanceReportFilterOptionDto> courseOptions = new ArrayList<>();
    private List<AttendanceReportFilterOptionDto> sectionOptions = new ArrayList<>();
    private List<AdminAttendanceSessionRowDto> attendanceSessions = new ArrayList<>();

    private Long selectedInstructorId;
    private Long selectedCourseId;
    private Long selectedSectionId;
    private Long selectedAttendanceSessionId;

    private String selectedSessionDate;
    private String selectedInstructorName;
    private String selectedCourseCode;
    private String selectedSectionLabel;

    private Integer sessionCount;
    private AdminAttendanceSelectedSessionDto selectedReportSession;

    public List<AttendanceReportFilterOptionDto> getInstructorOptions() {
        return instructorOptions;
    }

    public void setInstructorOptions(List<AttendanceReportFilterOptionDto> instructorOptions) {
        this.instructorOptions = instructorOptions;
    }

    public List<AttendanceReportFilterOptionDto> getCourseOptions() {
        return courseOptions;
    }

    public void setCourseOptions(List<AttendanceReportFilterOptionDto> courseOptions) {
        this.courseOptions = courseOptions;
    }

    public List<AttendanceReportFilterOptionDto> getSectionOptions() {
        return sectionOptions;
    }

    public void setSectionOptions(List<AttendanceReportFilterOptionDto> sectionOptions) {
        this.sectionOptions = sectionOptions;
    }

    public List<AdminAttendanceSessionRowDto> getAttendanceSessions() {
        return attendanceSessions;
    }

    public void setAttendanceSessions(List<AdminAttendanceSessionRowDto> attendanceSessions) {
        this.attendanceSessions = attendanceSessions;
    }

    public Long getSelectedInstructorId() {
        return selectedInstructorId;
    }

    public void setSelectedInstructorId(Long selectedInstructorId) {
        this.selectedInstructorId = selectedInstructorId;
    }

    public Long getSelectedCourseId() {
        return selectedCourseId;
    }

    public void setSelectedCourseId(Long selectedCourseId) {
        this.selectedCourseId = selectedCourseId;
    }

    public Long getSelectedSectionId() {
        return selectedSectionId;
    }

    public void setSelectedSectionId(Long selectedSectionId) {
        this.selectedSectionId = selectedSectionId;
    }

    public Long getSelectedAttendanceSessionId() {
        return selectedAttendanceSessionId;
    }

    public void setSelectedAttendanceSessionId(Long selectedAttendanceSessionId) {
        this.selectedAttendanceSessionId = selectedAttendanceSessionId;
    }

    public String getSelectedSessionDate() {
        return selectedSessionDate;
    }

    public void setSelectedSessionDate(String selectedSessionDate) {
        this.selectedSessionDate = selectedSessionDate;
    }

    public String getSelectedInstructorName() {
        return selectedInstructorName;
    }

    public void setSelectedInstructorName(String selectedInstructorName) {
        this.selectedInstructorName = selectedInstructorName;
    }

    public String getSelectedCourseCode() {
        return selectedCourseCode;
    }

    public void setSelectedCourseCode(String selectedCourseCode) {
        this.selectedCourseCode = selectedCourseCode;
    }

    public String getSelectedSectionLabel() {
        return selectedSectionLabel;
    }

    public void setSelectedSectionLabel(String selectedSectionLabel) {
        this.selectedSectionLabel = selectedSectionLabel;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public AdminAttendanceSelectedSessionDto getSelectedReportSession() {
        return selectedReportSession;
    }

    public void setSelectedReportSession(AdminAttendanceSelectedSessionDto selectedReportSession) {
        this.selectedReportSession = selectedReportSession;
    }
}