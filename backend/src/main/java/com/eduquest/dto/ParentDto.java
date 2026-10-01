package com.eduquest.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParentDto {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private List<StudentDto> students = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ParentDto() {}

    public ParentDto(Long id, Long userId, String username, String fullName, List<StudentDto> students, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.students = students != null ? students : new ArrayList<>();
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

    public List<StudentDto> getStudents() { return students; }
    public void setStudents(List<StudentDto> students) { this.students = students; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ParentDtoBuilder builder() {
        return new ParentDtoBuilder();
    }

    public static class ParentDtoBuilder {
        private Long id;
        private Long userId;
        private String username;
        private String fullName;
        private List<StudentDto> students = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ParentDtoBuilder id(Long id) { this.id = id; return this; }
        public ParentDtoBuilder userId(Long userId) { this.userId = userId; return this; }
        public ParentDtoBuilder username(String username) { this.username = username; return this; }
        public ParentDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public ParentDtoBuilder students(List<StudentDto> students) { this.students = students; return this; }
        public ParentDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ParentDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ParentDto build() {
            return new ParentDto(id, userId, username, fullName, students, createdAt, updatedAt);
        }
    }
}
