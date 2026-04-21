package com.bridgetrack.bridgetrack.dto;

public class PlannerSectionDto {

    private Long id;
    private String sectionCode;
    private String schedule;
    private String modality;
    private String term;
    private Long courseId;
    private String courseName;
    private String instructorName;
    private boolean conflictTerm1;
    private boolean conflictTerm2;

    public PlannerSectionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public boolean isConflictTerm1() {
        return conflictTerm1;
    }

    public void setConflictTerm1(boolean conflictTerm1) {
        this.conflictTerm1 = conflictTerm1;
    }

    public boolean isConflictTerm2() {
        return conflictTerm2;
    }

    public void setConflictTerm2(boolean conflictTerm2) {
        this.conflictTerm2 = conflictTerm2;
    }
}