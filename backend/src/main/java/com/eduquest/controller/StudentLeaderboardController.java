package com.eduquest.controller;

import com.eduquest.domain.Student;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.LeaderboardEntryDto;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/leaderboard")
public class StudentLeaderboardController {

    private final LeaderboardService leaderboardService;
    private final StudentRepository studentRepository;

    public StudentLeaderboardController(LeaderboardService leaderboardService, StudentRepository studentRepository) {
        this.leaderboardService = leaderboardService;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(
            @RequestParam(defaultValue = "CLASSROOM") String scope,
            Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        Student student = studentRepository.findByUserAccountUsername(user.getUsername()).orElse(null);
        Long studentId = student != null ? student.getId() : null;

        return ResponseEntity.ok(leaderboardService.getLeaderboard(studentId, scope));
    }
}
