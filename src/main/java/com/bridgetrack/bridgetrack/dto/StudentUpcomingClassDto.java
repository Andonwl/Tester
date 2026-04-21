package com.bridgetrack.bridgetrack.dto;

public class StudentUpcomingClassDto {

    private Long plannedCourseId;
    private String courseCode;
    private String courseTitle;
    private String semesterLabel;
    private Integer termNumber;

    public StudentUpcomingClassDto() {
    }

    public StudentUpcomingClassDto(Long plannedCourseId, String courseCode, String courseTitle, String semesterLabel, Integer termNumber) {
        this.plannedCourseId = plannedCourseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.semesterLabel = semesterLabel;
        this.termNumber = termNumber;
    }

    public Long getPlannedCourseId() {
        return plannedCourseId;
    }

    public void setPlannedCourseId(Long plannedCourseId) {
        this.plannedCourseId = plannedCourseId;
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

    public String getSemesterLabel() {
        return semesterLabel;
    }

    public void setSemesterLabel(String semesterLabel) {
        this.semesterLabel = semesterLabel;
    }

    public Integer getTermNumber() {
        return termNumber;
    }

    public void setTermNumber(Integer termNumber) {
        this.termNumber = termNumber;
    }
}