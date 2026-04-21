package com.bridgetrack.bridgetrack.dto;

import java.util.List;

public class PlannerCourseDto {
    private Long id;
    private String courseCode;
    private String courseName;
    private String programName;
    private Integer credits;
    private List<String> prerequisites;
    private String termAvailability;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public List<String> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<String> prerequisites) { this.prerequisites = prerequisites; }

    public String getTermAvailability() { return termAvailability; }
    public void setTermAvailability(String termAvailability) { this.termAvailability = termAvailability; }
}