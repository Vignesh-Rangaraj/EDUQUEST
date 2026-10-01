package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.domain.Teacher;
import com.eduquest.dto.StudentDto;
import com.eduquest.dto.TeacherDto;
import com.eduquest.repository.StudentRepository;
import com.eduquest.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AdminService adminService;

    public TeacherService(TeacherRepository teacherRepository, StudentRepository studentRepository, AdminService adminService) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.adminService = adminService;
    }

    @Transactional(readOnly = true)
    public TeacherDto getTeacherProfileByUsername(String username) {
        Teacher teacher = teacherRepository.findByUserAccountUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found for user: " + username));
        return adminService.mapToTeacherDto(teacher);
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsInTeacherClassroom(String username) {
        Teacher teacher = teacherRepository.findByUserAccountUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found for user: " + username));

        if (teacher.getClassroom() == null) {
            return List.of();
        }

        List<Student> students = studentRepository.findByClassroom_Id(teacher.getClassroom().getId());
        return students.stream().map(adminService::mapToStudentDto).collect(Collectors.toList());
    }
}
