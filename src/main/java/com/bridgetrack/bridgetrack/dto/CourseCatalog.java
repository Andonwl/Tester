package com.bridgetrack.bridgetrack.dto;

public class CourseCatalog {
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private String modality;
    private boolean requiresPrereq;
    private String category;

    private Long programId;
    private String programName;

    public CourseCatalog() {}

    public CourseCatalog(
            Long courseId,
            String courseCode,
            String courseName,
            String description,
            String modality,
            boolean requiresPrereq,
            String category,
            Long programId,
            String programName
    ) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.modality = modality;
        this.requiresPrereq = requiresPrereq;
        this.category = category;
        this.programId = programId;
        this.programName = programName;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getModality() { return modality; }
    public void setModality(String modality) { this.modality = modality; }

    public boolean isRequiresPrereq() { return requiresPrereq; }
    public void setRequiresPrereq(boolean requiresPrereq) { this.requiresPrereq = requiresPrereq; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getProgramId() { return programId; }
    public void setProgramId(Long programId) { this.programId = programId; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }
}