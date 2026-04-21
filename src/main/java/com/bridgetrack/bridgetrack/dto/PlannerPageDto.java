package com.bridgetrack.bridgetrack.dto;

import java.util.List;

public class PlannerPageDto {

    private PlannerStudentDto student;
    private List<PlannerCourseDto> courses;
    private List<PlannerSectionDto> sections;
    private List<PlannerEnrollmentDto> term1;
    private List<PlannerEnrollmentDto> term2;

    public PlannerStudentDto getStudent() {
        return student;
    }

    public void setStudent(PlannerStudentDto student) {
        this.student = student;
    }

    public List<PlannerCourseDto> getCourses() {
        return courses;
    }

    public void setCourses(List<PlannerCourseDto> courses) {
        this.courses = courses;
    }

    public List<PlannerSectionDto> getSections() {
        return sections;
    }

    public void setSections(List<PlannerSectionDto> sections) {
        this.sections = sections;
    }

    public List<PlannerEnrollmentDto> getTerm1() {
        return term1;
    }

    public void setTerm1(List<PlannerEnrollmentDto> term1) {
        this.term1 = term1;
    }

    public List<PlannerEnrollmentDto> getTerm2() {
        return term2;
    }

    public void setTerm2(List<PlannerEnrollmentDto> term2) {
        this.term2 = term2;
    }
}