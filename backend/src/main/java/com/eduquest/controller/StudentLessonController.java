package com.eduquest.controller;

import com.eduquest.domain.Activity;
import com.eduquest.domain.Student;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.ActivityDto;
import com.eduquest.dto.ContinueLearningDto;
import com.eduquest.repository.ActivityRepository;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentLessonController {

    private final ActivityService activityService;
    private final ActivityRepository activityRepository;
    private final StudentRepository studentRepository;

    public StudentLessonController(ActivityService activityService, ActivityRepository activityRepository, StudentRepository studentRepository) {
        this.activityService = activityService;
        this.activityRepository = activityRepository;
        this.studentRepository = studentRepository;
    }

    private Student getStudent(Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        return studentRepository.findByUserAccountUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Student profile not found for user: " + user.getUsername()));
    }

    @GetMapping("/lessons/{id}")
    public ResponseEntity<ActivityDto> getLessonDetails(@PathVariable Long id, Authentication authentication) {
        Student student = getStudent(authentication);
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        ActivityDto dto = ActivityDto.fromEntity(activity);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/lessons/{id}/complete")
    public ResponseEntity<ActivityDto> completeLesson(@PathVariable Long id, Authentication authentication) {
        Student student = getStudent(authentication);
        return ResponseEntity.ok(activityService.completeLesson(student.getId(), id));
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<List<ActivityDto>> getLessonsForModule(@PathVariable Long moduleId, Authentication authentication) {
        Student student = getStudent(authentication);
        return ResponseEntity.ok(activityService.getLessonsForModule(moduleId, student.getId()));
    }

    @GetMapping("/continue-learning")
    public ResponseEntity<ContinueLearningDto> getContinueLearning(Authentication authentication) {
        Student student = getStudent(authentication);
        return ResponseEntity.ok(activityService.getContinueLearning(student.getId()));
    }
}
