package com.eduquest.controller;

import com.eduquest.domain.UserAccount;
import com.eduquest.dto.StudentDto;
import com.eduquest.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentDto> getProfile(Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        return ResponseEntity.ok(studentService.getStudentProfileByUsername(user.getUsername()));
    }
}
