package com.eduquest.controller;

import com.eduquest.domain.Student;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.AchievementDto;
import com.eduquest.dto.StudentOverviewProgressDto;
import com.eduquest.dto.StudentProgressDto;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.StudentProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentProgressController {

    private final StudentProgressService progressService;
    private final StudentRepository studentRepository;

    public StudentProgressController(StudentProgressService progressService, StudentRepository studentRepository) {
        this.progressService = progressService;
        this.studentRepository = studentRepository;
    }

    private Student getStudent(Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        return studentRepository.findByUserAccountUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Student profile not found for user: " + user.getUsername()));
    }

    @GetMapping("/progress")
    public ResponseEntity<List<StudentProgressDto>> getMyProgress(Authentication authentication) {
        Student student = getStudent(authentication);
        return ResponseEntity.ok(progressService.getProgressByStudent(student.getId()));
    }

    @GetMapping("/progress/overview")
    public ResponseEntity<StudentOverviewProgressDto> getMyProgressOverview(Authentication authentication) {
        Student student = getStudent(authentication);
        return ResponseEntity.ok(progressService.getStudentOverview(student.getId()));
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementDto>> getMyAchievements(Authentication authentication) {
        Student student = getStudent(authentication);
        return ResponseEntity.ok(progressService.getStudentAchievements(student.getId()));
    }

    @PostMapping("/progress")
    public ResponseEntity<StudentProgressDto> saveProgress(@RequestBody StudentProgressDto dto, Authentication authentication) {
        Student student = getStudent(authentication);
        StudentProgressDto saved = progressService.saveProgress(
                student.getId(),
                dto.getActivityId(),
                dto.getScore(),
                dto.isCompleted(),
                dto.getCompletedAt()
        );
        return ResponseEntity.ok(saved);
    }
}
