package com.bridgetrack.bridgetrack.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "code", unique = true, nullable = false)
    private String courseCode;

    @Column(name = "title", nullable = false)
    private String courseName;

    @Column(name = "description")
    private String description;
    
    @Column(name = "modality")
    private String modality;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;
    
    @Column(name = "requires_prereq", nullable = false)
    private boolean requiresPrereq;
    
    //May need to get rid of this.
    @Column(nullable = false)
    private String category;

    public Course() {}

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }
    
    public String getModality() {return modality;}
    public void setModality(String modality) {this.modality = modality;}
    
    public boolean isRequiresPrereq() { return requiresPrereq;}
    public void setRequiresPrereq(boolean requiresPrereq) {this.requiresPrereq = requiresPrereq; }

    //May need to get rid of
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

}


