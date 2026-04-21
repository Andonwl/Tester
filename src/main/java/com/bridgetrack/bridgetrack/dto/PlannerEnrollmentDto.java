package com.bridgetrack.bridgetrack.dto;

public class PlannerEnrollmentDto {
    private Long sectionId;
    private String sectionCode;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Integer credits;
    private String schedule;
    private String modality;
    private String instructorName;
    private String term;

    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }

    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public String getModality() { return modality; }
    public void setModality(String modality) { this.modality = modality; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
}