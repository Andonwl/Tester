package com.bridgetrack.bridgetrack.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "student_email")
    private String studentEmail;

    @Column(name = "personal_email")
    private String personalEmail;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Column(name = "status")
    private String status;

    @Column(name = "registered_at")
    private LocalDate registeredAt;

    @Column(name = "possible_duplicate")
    private boolean possibleDuplicate;

    @Column(name = "password_hash")
    private String passwordHash;

    public Student() {
    }

    public Student(String firstName, String lastName, String email, String phone, LocalDate birthDate, String passwordHash) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentEmail = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.passwordHash = passwordHash;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDate registeredAt) {
        this.registeredAt = registeredAt;
    }

    
    public boolean isPossibleDuplicate() {
        return possibleDuplicate;
    }

    public void setPossibleDuplicate(boolean possibleDuplicate) {
        this.possibleDuplicate = possibleDuplicate;
    }
    

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}