package com.eduquest.controller;

import com.eduquest.domain.Student;
import com.eduquest.domain.StudentQuizAttempt;
import com.eduquest.domain.UserAccount;
import com.eduquest.dto.SubmitQuizRequest;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/quiz")
public class StudentQuizController {

    private final QuizService quizService;
    private final StudentRepository studentRepository;

    public StudentQuizController(QuizService quizService, StudentRepository studentRepository) {
        this.quizService = quizService;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/submit")
    public ResponseEntity<StudentQuizAttempt> submitQuiz(@RequestBody SubmitQuizRequest request, Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        Student student = studentRepository.findByUserAccountUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Student profile not found for user: " + user.getUsername()));

        StudentQuizAttempt attempt = quizService.submitQuiz(student.getId(), request);
        return ResponseEntity.ok(attempt);
    }
}
