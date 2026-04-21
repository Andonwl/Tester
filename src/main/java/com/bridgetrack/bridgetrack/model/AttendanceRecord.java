package com.bridgetrack.bridgetrack.model;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attendance_session_id", nullable = false)
    private AttendanceSessions attendanceSession;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    public Long getId() {
        return id;
    }

    public AttendanceSessions getAttendanceSession() {
        return attendanceSession;
    }

    public void setAttendanceSession(AttendanceSessions attendanceSession) {
        this.attendanceSession = attendanceSession;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}