package com.bridgetrack.bridgetrack.dto;

public class AttendancePreviewStudentDto {

    private String studentName;
    private String status;

    public AttendancePreviewStudentDto() {
    }

    public AttendancePreviewStudentDto(String studentName, String status) {
        this.studentName = studentName;
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}