package com.eduquest.dto;

import java.time.LocalDateTime;

public class TeacherDto {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private Long classroomId;
    private String classroomName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TeacherDto() {}

    public TeacherDto(Long id, Long userId, String username, String fullName, Long classroomId, String classroomName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.classroomId = classroomId;
        this.classroomName = classroomName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

    public String getClassroomName() { return classroomName; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static TeacherDtoBuilder builder() {
        return new TeacherDtoBuilder();
    }

    public static class TeacherDtoBuilder {
        private Long id;
        private Long userId;
        private String username;
        private String fullName;
        private Long classroomId;
        private String classroomName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public TeacherDtoBuilder id(Long id) { this.id = id; return this; }
        public TeacherDtoBuilder userId(Long userId) { this.userId = userId; return this; }
        public TeacherDtoBuilder username(String username) { this.username = username; return this; }
        public TeacherDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public TeacherDtoBuilder classroomId(Long classroomId) { this.classroomId = classroomId; return this; }
        public TeacherDtoBuilder classroomName(String classroomName) { this.classroomName = classroomName; return this; }
        public TeacherDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TeacherDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public TeacherDto build() {
            return new TeacherDto(id, userId, username, fullName, classroomId, classroomName, createdAt, updatedAt);
        }
    }
}
