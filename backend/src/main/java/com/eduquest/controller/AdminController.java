package com.eduquest.controller;

import com.eduquest.dto.*;
import com.eduquest.service.AdminAnalyticsService;
import com.eduquest.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminAnalyticsService adminAnalyticsService;

    public AdminController(AdminService adminService, AdminAnalyticsService adminAnalyticsService) {
        this.adminService = adminService;
        this.adminAnalyticsService = adminAnalyticsService;
    }

    @PostMapping("/teachers")
    public ResponseEntity<TeacherDto> createTeacher(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createTeacher(request));
    }

    @PostMapping("/students")
    public ResponseEntity<StudentDto> createStudent(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createStudent(request));
    }

    @PostMapping("/parents")
    public ResponseEntity<ParentDto> createParent(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createParent(request));
    }

    @PostMapping("/superadmins")
    public ResponseEntity<UserAccountDto> createSuperAdmin(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createSuperAdmin(request));
    }

    @PostMapping("/teachers/assign-classroom")
    public ResponseEntity<TeacherDto> assignTeacherToClassroom(@RequestBody AssignTeacherRequest request) {
        return ResponseEntity.ok(adminService.assignTeacherToClassroom(request));
    }

    @PostMapping("/students/assign-parent")
    public ResponseEntity<StudentDto> assignParentToStudent(@RequestBody AssignParentRequest request) {
        return ResponseEntity.ok(adminService.assignParentToStudent(request));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(adminService.updateStudent(id, request));
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<TeacherDto> updateTeacher(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(adminService.updateTeacher(id, request));
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        return ResponseEntity.ok(adminService.getAllTeachers());
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @GetMapping("/parents")
    public ResponseEntity<List<ParentDto>> getAllParents() {
        return ResponseEntity.ok(adminService.getAllParents());
    }

    @GetMapping("/superadmins")
    public ResponseEntity<List<UserAccountDto>> getAllSuperAdmins() {
        return ResponseEntity.ok(adminService.getAllSuperAdmins());
    }

    @GetMapping("/classrooms")
    public ResponseEntity<List<ClassroomDto>> getAllClassrooms() {
        return ResponseEntity.ok(adminService.getAllClassrooms());
    }

    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsDto> getAnalytics() {
        return ResponseEntity.ok(adminAnalyticsService.getAdminAnalytics());
    }
}
