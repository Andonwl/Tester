package com.bridgetrack.bridgetrack.dto;

public class CreatePrerequisiteRequest {
    private Long courseId;
    private Long prerequisiteCourseId;

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getPrerequisiteCourseId() { return prerequisiteCourseId; }
    public void setPrerequisiteCourseId(Long prerequisiteCourseId) { this.prerequisiteCourseId = prerequisiteCourseId; }
}
