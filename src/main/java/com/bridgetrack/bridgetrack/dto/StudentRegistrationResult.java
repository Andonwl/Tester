package com.bridgetrack.bridgetrack.dto;

public class StudentRegistrationResult {

    private Long studentId;
    private String studentEmail;
    private String rawPassword;

    public StudentRegistrationResult(Long studentId, String studentEmail, String rawPassword) {
        this.studentId = studentId;
        this.studentEmail = studentEmail;
        this.rawPassword = rawPassword;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }
}