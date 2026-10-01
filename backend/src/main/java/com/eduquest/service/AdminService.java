package com.eduquest.service;

import com.eduquest.domain.*;
import com.eduquest.dto.*;
import com.eduquest.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserAccountRepository userAccountRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final ClassroomRepository classroomRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(
            UserAccountRepository userAccountRepository,
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            ParentRepository parentRepository,
            ClassroomRepository classroomRepository,
            PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.classroomRepository = classroomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TeacherDto createTeacher(CreateUserRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        UserAccount user = userAccountRepository.save(UserAccount.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.TEACHER)
                .build());

        Classroom classroom = null;
        if (request.getClassroomId() != null) {
            classroom = classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + request.getClassroomId()));
        }

        Teacher teacher = Teacher.builder()
                .userAccount(user)
                .classroom(classroom)
                .build();

        Teacher saved = teacherRepository.save(teacher);
        return mapToTeacherDto(saved);
    }

    @Transactional
    public StudentDto createStudent(CreateUserRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        UserAccount user = userAccountRepository.save(UserAccount.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.STUDENT)
                .build());

        Classroom classroom = null;
        if (request.getClassroomId() != null) {
            classroom = classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + request.getClassroomId()));
        }

        Parent parent = null;
        if (request.getParentId() != null) {
            parent = parentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found: " + request.getParentId()));
        }

        Student student = Student.builder()
                .userAccount(user)
                .classroom(classroom)
                .parent(parent)
                .build();

        Student saved = studentRepository.save(student);
        return mapToStudentDto(saved);
    }

    @Transactional
    public ParentDto createParent(CreateUserRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        UserAccount user = userAccountRepository.save(UserAccount.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.PARENT)
                .build());

        Parent parent = Parent.builder()
                .userAccount(user)
                .build();

        Parent saved = parentRepository.save(parent);
        return mapToParentDto(saved);
    }

    @Transactional
    public UserAccountDto createSuperAdmin(CreateUserRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        UserAccount user = userAccountRepository.save(UserAccount.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.SUPER_ADMIN)
                .build());

        return mapToUserAccountDto(user);
    }

    @Transactional
    public TeacherDto assignTeacherToClassroom(AssignTeacherRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + request.getTeacherId()));

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + request.getClassroomId()));

        teacher.setClassroom(classroom);
        Teacher updated = teacherRepository.save(teacher);
        return mapToTeacherDto(updated);
    }

    @Transactional
    public StudentDto assignParentToStudent(AssignParentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + request.getStudentId()));

        Parent parent = parentRepository.findById(request.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent not found: " + request.getParentId()));

        student.setParent(parent);
        Student updated = studentRepository.save(student);
        return mapToStudentDto(updated);
    }

    @Transactional
    public StudentDto updateStudent(Long studentId, UpdateUserRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            student.getUserAccount().setFullName(request.getFullName().trim());
            userAccountRepository.save(student.getUserAccount());
        }

        if (request.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + request.getClassroomId()));
            student.setClassroom(classroom);
        }

        if (request.getParentId() != null) {
            Parent parent = parentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found: " + request.getParentId()));
            student.setParent(parent);
        }

        Student updated = studentRepository.save(student);
        return mapToStudentDto(updated);
    }

    @Transactional
    public TeacherDto updateTeacher(Long teacherId, UpdateUserRequest request) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            teacher.getUserAccount().setFullName(request.getFullName().trim());
            userAccountRepository.save(teacher.getUserAccount());
        }

        if (request.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findById(request.getClassroomId())
                    .orElseThrow(() -> new IllegalArgumentException("Classroom not found: " + request.getClassroomId()));
            teacher.setClassroom(classroom);
        }

        Teacher updated = teacherRepository.save(teacher);
        return mapToTeacherDto(updated);
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> getAllTeachers() {
        return teacherRepository.findAll().stream().map(this::mapToTeacherDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream().map(this::mapToStudentDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParentDto> getAllParents() {
        return parentRepository.findAll().stream().map(this::mapToParentDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClassroomDto> getAllClassrooms() {
        return classroomRepository.findAll().stream().map(c -> ClassroomDto.builder()
                .id(c.getId())
                .grade(c.getGrade())
                .section(c.getSection())
                .name(c.getName())
                .schoolName(c.getSchool() != null ? c.getSchool().getName() : "")
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build()).collect(Collectors.toList());
    }

    public TeacherDto mapToTeacherDto(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .userId(teacher.getUserAccount().getId())
                .username(teacher.getUserAccount().getUsername())
                .fullName(teacher.getUserAccount().getFullName())
                .classroomId(teacher.getClassroom() != null ? teacher.getClassroom().getId() : null)
                .classroomName(teacher.getClassroom() != null ? teacher.getClassroom().getName() : "Unassigned")
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
    }

    public StudentDto mapToStudentDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .userId(student.getUserAccount().getId())
                .username(student.getUserAccount().getUsername())
                .fullName(student.getUserAccount().getFullName())
                .classroomId(student.getClassroom() != null ? student.getClassroom().getId() : null)
                .classroomName(student.getClassroom() != null ? student.getClassroom().getName() : "Unassigned")
                .parentId(student.getParent() != null ? student.getParent().getId() : null)
                .parentFullName(student.getParent() != null ? student.getParent().getUserAccount().getFullName() : "Unassigned")
                .xp(student.getXp() != null ? student.getXp() : 0)
                .level(student.getLevel() != null ? student.getLevel() : 1)
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    public ParentDto mapToParentDto(Parent parent) {
        List<StudentDto> childDtos = parent.getStudents() != null
                ? parent.getStudents().stream().map(this::mapToStudentDto).collect(Collectors.toList())
                : List.of();

        return ParentDto.builder()
                .id(parent.getId())
                .userId(parent.getUserAccount().getId())
                .username(parent.getUserAccount().getUsername())
                .fullName(parent.getUserAccount().getFullName())
                .students(childDtos)
                .createdAt(parent.getCreatedAt())
                .updatedAt(parent.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserAccountDto> getAllSuperAdmins() {
        return userAccountRepository.findByRole(Role.SUPER_ADMIN).stream()
                .map(this::mapToUserAccountDto)
                .collect(Collectors.toList());
    }

    public UserAccountDto mapToUserAccountDto(UserAccount user) {
        return UserAccountDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
