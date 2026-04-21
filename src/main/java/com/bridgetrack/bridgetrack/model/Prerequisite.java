package com.bridgetrack.bridgetrack.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "prerequisites",
    uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "prereq_course_id"})
)
public class Prerequisite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "prereq_course_id", nullable = false)
    private Course prerequisiteCourse;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Prerequisite() {}

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getPrerequisiteCourse() {
        return prerequisiteCourse;
    }

    public void setPrerequisiteCourse(Course prerequisiteCourse) {
        this.prerequisiteCourse = prerequisiteCourse;
    }
}