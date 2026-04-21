package com.bridgetrack.bridgetrack.dto;

public class PlannerEnrollmentRequestDto {

    private Long studentId;
    private Long sectionId;
    private String plannedTerm;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getPlannedTerm() {
        return plannedTerm;
    }

    public void setPlannedTerm(String plannedTerm) {
        this.plannedTerm = plannedTerm;
    }
}