package com.bridgetrack.bridgetrack.model;

import jakarta.persistence.*;

@Entity
@Table(name = "planned_courses")
public class PlannedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planned_course_id")
    private Long plannedCourseId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "term_number")
    private Integer termNumber;

    @Column(name = "semester_label")
    private String semesterLabel;

    @Column(name = "enrolled")
    private boolean enrolled;

    public PlannedCourse() {
    }

    public Long getPlannedCourseId() {
        return plannedCourseId;
    }

    public void setPlannedCourseId(Long plannedCourseId) {
        this.plannedCourseId = plannedCourseId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getTermNumber() {
        return termNumber;
    }

    public void setTermNumber(Integer termNumber) {
        this.termNumber = termNumber;
    }

    public String getSemesterLabel() {
        return semesterLabel;
    }

    public void setSemesterLabel(String semesterLabel) {
        this.semesterLabel = semesterLabel;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }
}