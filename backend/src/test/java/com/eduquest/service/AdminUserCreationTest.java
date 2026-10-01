package com.eduquest.service;

import com.eduquest.config.DataInitializer;
import com.eduquest.domain.Classroom;
import com.eduquest.domain.Parent;
import com.eduquest.dto.*;
import com.eduquest.repository.ClassroomRepository;
import com.eduquest.repository.ParentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AdminUserCreationTest {

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private ParentRepository parentRepository;

    @BeforeEach
    public void setUp() {
        dataInitializer.run();
    }

    @Test
    public void testCreateStudentSuccessfully() {
        Classroom classroom = classroomRepository.findAll().get(0);
        Parent parent = parentRepository.findAll().get(0);

        CreateUserRequest request = CreateUserRequest.builder()
                .fullName("Nathan Kumar")
                .username("@student_nathan_new")
                .password("password123")
                .classroomId(classroom.getId())
                .parentId(parent.getId())
                .build();

        StudentDto created = adminService.createStudent(request);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("@student_nathan_new", created.getUsername());
        assertEquals("Nathan Kumar", created.getFullName());
        assertEquals(classroom.getId(), created.getClassroomId());
        assertEquals(parent.getId(), created.getParentId());
    }

    @Test
    public void testCreateTeacherSuccessfully() {
        Classroom classroom = classroomRepository.findAll().get(0);

        CreateUserRequest request = CreateUserRequest.builder()
                .fullName("New Teacher")
                .username("teacher_new_test")
                .password("password123")
                .classroomId(classroom.getId())
                .build();

        TeacherDto created = adminService.createTeacher(request);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("teacher_new_test", created.getUsername());
        assertEquals("New Teacher", created.getFullName());
    }

    @Test
    public void testCreateParentSuccessfully() {
        CreateUserRequest request = CreateUserRequest.builder()
                .fullName("New Parent")
                .username("parent_new_test")
                .password("password123")
                .build();

        ParentDto created = adminService.createParent(request);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("parent_new_test", created.getUsername());
        assertEquals("New Parent", created.getFullName());
    }

    @Test
    public void testCreateStudentWithDuplicateUsernameFails() {
        CreateUserRequest request = CreateUserRequest.builder()
                .fullName("Duplicate Test")
                .username("student_6a_1") // Existing user seeded by DataInitializer
                .password("password123")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adminService.createStudent(request);
        });

        assertTrue(exception.getMessage().contains("Username already exists"));
    }
}
