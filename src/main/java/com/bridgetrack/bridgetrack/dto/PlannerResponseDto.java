package com.bridgetrack.bridgetrack.dto;

import java.util.List;

public class PlannerResponseDto {
    private Long studentId;
    private List<PlannerEnrollmentDto> term1;
    private List<PlannerEnrollmentDto> term2;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public List<PlannerEnrollmentDto> getTerm1() { return term1; }
    public void setTerm1(List<PlannerEnrollmentDto> term1) { this.term1 = term1; }

    public List<PlannerEnrollmentDto> getTerm2() { return term2; }
    public void setTerm2(List<PlannerEnrollmentDto> term2) { this.term2 = term2; }
}