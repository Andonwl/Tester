package com.bridgetrack.bridgetrack.dto;

import com.bridgetrack.bridgetrack.model.EnrollmentStatus;

public class UpdateEnrollmentStatusRequest {
    private EnrollmentStatus status;

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
}
