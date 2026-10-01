package com.eduquest.controller;

import com.eduquest.domain.Student;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.ActivityDto;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/activities")
public class StudentActivityController {

    private final ActivityService activityService;
    private final StudentRepository studentRepository;

    public StudentActivityController(ActivityService activityService, StudentRepository studentRepository) {
        this.activityService = activityService;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public ResponseEntity<List<ActivityDto>> getPublishedActivities(Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        Student student = studentRepository.findByUserAccountUsername(user.getUsername()).orElse(null);
        Long studentId = student != null ? student.getId() : null;

        return ResponseEntity.ok(activityService.getPublishedActivitiesForStudent(studentId));
    }
}
