package com.eduquest.service;

import com.eduquest.domain.Parent;
import com.eduquest.dto.ParentDto;
import com.eduquest.dto.ParentChildActivityAnalyticsDto;
import com.eduquest.dto.StudentOverviewProgressDto;
import com.eduquest.repository.ParentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParentService {

    private final ParentRepository parentRepository;
    private final AdminService adminService;
    private final StudentProgressService studentProgressService;
    private final ParentChildActivityAnalyticsService activityAnalyticsService;

    public ParentService(ParentRepository parentRepository, AdminService adminService, StudentProgressService studentProgressService,
            ParentChildActivityAnalyticsService activityAnalyticsService) {
        this.parentRepository = parentRepository;
        this.adminService = adminService;
        this.studentProgressService = studentProgressService;
        this.activityAnalyticsService = activityAnalyticsService;
    }

    @Transactional(readOnly = true)
    public ParentDto getParentProfileByUsername(String username) {
        Parent parent = parentRepository.findByUserAccountUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Parent profile not found for user: " + username));
        return adminService.mapToParentDto(parent);
    }

    @Transactional(readOnly = true)
    public StudentOverviewProgressDto getChildOverview(String username, Long studentId) {
        Parent parent = parentRepository.findByUserAccountUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Parent profile not found for user: " + username));

        boolean isLinkedChild = parent.getStudents() != null &&
                parent.getStudents().stream().anyMatch(s -> s.getId().equals(studentId));

        if (!isLinkedChild) {
            throw new IllegalArgumentException("Child #" + studentId + " is not linked to parent: " + username);
        }

        return studentProgressService.getStudentOverview(studentId);
    }

    @Transactional(readOnly = true)
    public ParentChildActivityAnalyticsDto getChildActivityAnalytics(String username, Long studentId) {
        Parent parent = parentRepository.findByUserAccountUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Parent profile not found for user: " + username));

        boolean isLinkedChild = parent.getStudents() != null &&
                parent.getStudents().stream().anyMatch(s -> s.getId().equals(studentId));
        if (!isLinkedChild) {
            throw new IllegalArgumentException("Child #" + studentId + " is not linked to parent: " + username);
        }
        return activityAnalyticsService.getAnalytics(studentId);
    }
}
