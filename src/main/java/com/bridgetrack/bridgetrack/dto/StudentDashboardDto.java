package com.bridgetrack.bridgetrack.dto;

import java.util.List;

public class StudentDashboardDto {

    private String upcomingTermName;
    private String upcomingTermDateRange;
    private List<StudentUpcomingClassDto> upcomingClasses;

    public StudentDashboardDto() {
    }

    public StudentDashboardDto(String upcomingTermName, String upcomingTermDateRange, List<StudentUpcomingClassDto> upcomingClasses) {
        this.upcomingTermName = upcomingTermName;
        this.upcomingTermDateRange = upcomingTermDateRange;
        this.upcomingClasses = upcomingClasses;
    }

    public String getUpcomingTermName() {
        return upcomingTermName;
    }

    public void setUpcomingTermName(String upcomingTermName) {
        this.upcomingTermName = upcomingTermName;
    }

    public String getUpcomingTermDateRange() {
        return upcomingTermDateRange;
    }

    public void setUpcomingTermDateRange(String upcomingTermDateRange) {
        this.upcomingTermDateRange = upcomingTermDateRange;
    }

    public List<StudentUpcomingClassDto> getUpcomingClasses() {
        return upcomingClasses;
    }

    public void setUpcomingClasses(List<StudentUpcomingClassDto> upcomingClasses) {
        this.upcomingClasses = upcomingClasses;
    }
}