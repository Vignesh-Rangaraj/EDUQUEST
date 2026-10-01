package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.dto.StudentDto;
import com.eduquest.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final AdminService adminService;

    public StudentService(StudentRepository studentRepository, AdminService adminService) {
        this.studentRepository = studentRepository;
        this.adminService = adminService;
    }

    @Transactional(readOnly = true)
    public StudentDto getStudentProfileByUsername(String username) {
        Student student = studentRepository.findByUserAccountUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found for user: " + username));
        return adminService.mapToStudentDto(student);
    }
}
