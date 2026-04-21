package com.bridgetrack.bridgetrack.service;

import com.bridgetrack.bridgetrack.dto.StudentDashboardDto;

public interface StudentDashboardService {
    StudentDashboardDto getDashboardData(String studentEmail);
}