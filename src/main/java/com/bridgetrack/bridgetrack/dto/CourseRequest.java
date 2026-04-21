package com.bridgetrack.bridgetrack.dto;

public class CourseRequest {

    private String courseCode;
    private String courseName;
    private String description;
    private String modality;
    private String category;
    private Boolean requiresPrereq;
    private Long programId;

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean isRequiresPrereq() {
        return Boolean.TRUE.equals(requiresPrereq);
    }

    public void setRequiresPrereq(Boolean requiresPrereq) {
        this.requiresPrereq = requiresPrereq;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }
}