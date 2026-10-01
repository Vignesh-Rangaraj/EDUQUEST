package com.eduquest.controller;

import com.eduquest.domain.Teacher;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.*;
import com.eduquest.repository.TeacherRepository;
import com.eduquest.service.ActivityService;
import com.eduquest.service.GameConfigurationService;
import com.eduquest.service.LessonService;
import com.eduquest.service.ModuleService;
import com.eduquest.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/modules")
public class TeacherModuleController {

    private final ModuleService moduleService;
    private final ActivityService activityService;
    private final LessonService lessonService;
    private final QuizService quizService;
    private final GameConfigurationService gameConfigService;
    private final TeacherRepository teacherRepository;

    public TeacherModuleController(
            ModuleService moduleService,
            ActivityService activityService,
            LessonService lessonService,
            QuizService quizService,
            GameConfigurationService gameConfigService,
            TeacherRepository teacherRepository) {
        this.moduleService = moduleService;
        this.activityService = activityService;
        this.lessonService = lessonService;
        this.quizService = quizService;
        this.gameConfigService = gameConfigService;
        this.teacherRepository = teacherRepository;
    }

    private Teacher getTeacher(Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        return teacherRepository.findByUserAccountUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user: " + user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<ModuleDto>> getMyModules(Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(moduleService.getModulesByTeacher(teacher.getId()));
    }

    @PostMapping
    public ResponseEntity<ModuleDto> createModule(@RequestBody CreateModuleRequest request, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        if (request.getClassroomId() == null && teacher.getClassroom() != null) {
            request.setClassroomId(teacher.getClassroom().getId());
        }
        return ResponseEntity.ok(moduleService.createModule(request, teacher.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleDto> updateModule(@PathVariable Long id, @RequestBody CreateModuleRequest request, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(moduleService.updateModule(id, request, teacher.getId()));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ModuleDto> publishModule(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(moduleService.publishModule(id, teacher.getId()));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ModuleDto> archiveModule(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        return ResponseEntity.ok(moduleService.archiveModule(id, teacher.getId()));
    }

    @PostMapping("/{activityId}/lesson")
    public ResponseEntity<LessonContentDto> saveLessonContent(@PathVariable Long activityId, @RequestBody LessonContentDto dto) {
        return ResponseEntity.ok(lessonService.saveLessonContent(activityId, dto.getContent(), dto.getEstimatedMinutes()));
    }

    @PostMapping("/{activityId}/quiz-question")
    public ResponseEntity<QuizQuestionDto> saveQuizQuestion(@PathVariable Long activityId, @RequestBody QuizQuestionDto dto) {
        return ResponseEntity.ok(quizService.saveQuestion(activityId, dto));
    }

    @GetMapping("/{moduleId}/lessons")
    public ResponseEntity<List<ActivityDto>> getLessonsForModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(activityService.getLessonsForModule(moduleId, null));
    }

    @PostMapping("/{moduleId}/lessons")
    public ResponseEntity<ActivityDto> createLessonInModule(
            @PathVariable Long moduleId,
            @RequestBody CreateActivityRequest request,
            Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        request.setModuleId(moduleId);
        return ResponseEntity.ok(activityService.createActivity(request, teacher.getId()));
    }

    @PostMapping("/{activityId}/game-config")
    public ResponseEntity<GameConfigurationDto> saveGameConfig(@PathVariable Long activityId, @RequestBody GameConfigurationDto dto) {
        return ResponseEntity.ok(gameConfigService.saveGameConfig(activityId, dto.getJsonConfiguration()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = getTeacher(authentication);
        moduleService.deleteModule(id, teacher.getId());
        return ResponseEntity.noContent().build();
    }
}
