package com.eduquest.controller;

import com.eduquest.domain.ActivityStatus;
import com.eduquest.domain.Student;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.*;
import com.eduquest.repository.ActivityRepository;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
public class StudentModuleController {

    private final ModuleService moduleService;
    private final ActivityRepository activityRepository;
    private final LessonService lessonService;
    private final QuizService quizService;
    private final GameConfigurationService gameConfigService;
    private final StudentModuleProgressService moduleProgressService;
    private final StudentRepository studentRepository;

    public StudentModuleController(
            ModuleService moduleService,
            ActivityRepository activityRepository,
            LessonService lessonService,
            QuizService quizService,
            GameConfigurationService gameConfigService,
            StudentModuleProgressService moduleProgressService,
            StudentRepository studentRepository) {
        this.moduleService = moduleService;
        this.activityRepository = activityRepository;
        this.lessonService = lessonService;
        this.quizService = quizService;
        this.gameConfigService = gameConfigService;
        this.moduleProgressService = moduleProgressService;
        this.studentRepository = studentRepository;
    }

    private Student getStudent(Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        return studentRepository.findByUserAccountUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Student profile not found for user: " + user.getUsername()));
    }

    @GetMapping("/modules")
    public ResponseEntity<List<ModuleDto>> getPublishedModules(Authentication authentication) {
        Student student = getStudent(authentication);
        return ResponseEntity.ok(moduleService.getPublishedModulesForStudent(student.getId()));
    }

    @GetMapping("/modules/{id}/activities")
    public ResponseEntity<List<ActivityDto>> getModuleActivities(@PathVariable Long id) {
        List<ActivityDto> activities = activityRepository.findAll().stream()
                .filter(a -> id.equals(a.getModuleId()) && a.getStatus() == ActivityStatus.PUBLISHED && a.isVisibleToStudents())
                .sorted(Comparator.comparingInt(a -> a.getDisplayOrder() != null ? a.getDisplayOrder() : 0))
                .map(ActivityDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/activities/{id}/lesson")
    public ResponseEntity<LessonContentDto> getLessonContent(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonContentByActivityId(id));
    }

    @GetMapping("/activities/{id}/quiz-questions")
    public ResponseEntity<List<QuizQuestionDto>> getQuizQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuestionsForActivity(id));
    }

    @GetMapping("/activities/{id}/game-config")
    public ResponseEntity<GameConfigurationDto> getGameConfig(@PathVariable Long id) {
        return ResponseEntity.ok(gameConfigService.getGameConfigByActivityId(id));
    }

    @GetMapping("/module-progress")
    public ResponseEntity<List<StudentModuleProgressDto>> getMyModuleProgress(Authentication authentication) {
        Student student = getStudent(authentication);
        return ResponseEntity.ok(moduleProgressService.getModuleProgressForStudent(student.getId()));
    }
}
