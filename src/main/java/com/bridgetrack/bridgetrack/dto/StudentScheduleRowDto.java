package com.bridgetrack.bridgetrack.dto;

public class StudentScheduleRowDto {

    private String termName;
    private String courseCode;
    private String courseTitle;
    private Long sectionId;
    private String meetingDays;
    private String startTime;
    private String endTime;
    private String modality;
    private String location;
    private String instructorName;
    private String status;

    public StudentScheduleRowDto() {
    }

    public StudentScheduleRowDto(String termName, String courseCode, String courseTitle, Long sectionId,
                                 String meetingDays, String startTime, String endTime, String modality,
                                 String location, String instructorName, String status) {
        this.termName = termName;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.sectionId = sectionId;
        this.meetingDays = meetingDays;
        this.startTime = startTime;
        this.endTime = endTime;
        this.modality = modality;
        this.location = location;
        this.instructorName = instructorName;
        this.status = status;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getMeetingDays() {
        return meetingDays;
    }

    public void setMeetingDays(String meetingDays) {
        this.meetingDays = meetingDays;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}