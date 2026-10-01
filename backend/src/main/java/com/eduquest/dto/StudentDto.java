package com.eduquest.dto;

import java.time.LocalDateTime;

public class StudentDto {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private Long classroomId;
    private String classroomName;
    private Long parentId;
    private String parentFullName;
    private Integer xp;
    private Integer level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StudentDto() {}

    public StudentDto(Long id, Long userId, String username, String fullName, Long classroomId, String classroomName, Long parentId, String parentFullName, Integer xp, Integer level, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.classroomId = classroomId;
        this.classroomName = classroomName;
        this.parentId = parentId;
        this.parentFullName = parentFullName;
        this.xp = xp;
        this.level = level;
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

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getParentFullName() { return parentFullName; }
    public void setParentFullName(String parentFullName) { this.parentFullName = parentFullName; }

    public Integer getXp() { return xp; }
    public void setXp(Integer xp) { this.xp = xp; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static StudentDtoBuilder builder() {
        return new StudentDtoBuilder();
    }

    public static class StudentDtoBuilder {
        private Long id;
        private Long userId;
        private String username;
        private String fullName;
        private Long classroomId;
        private String classroomName;
        private Long parentId;
        private String parentFullName;
        private Integer xp;
        private Integer level;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public StudentDtoBuilder id(Long id) { this.id = id; return this; }
        public StudentDtoBuilder userId(Long userId) { this.userId = userId; return this; }
        public StudentDtoBuilder username(String username) { this.username = username; return this; }
        public StudentDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public StudentDtoBuilder classroomId(Long classroomId) { this.classroomId = classroomId; return this; }
        public StudentDtoBuilder classroomName(String classroomName) { this.classroomName = classroomName; return this; }
        public StudentDtoBuilder parentId(Long parentId) { this.parentId = parentId; return this; }
        public StudentDtoBuilder parentFullName(String parentFullName) { this.parentFullName = parentFullName; return this; }
        public StudentDtoBuilder xp(Integer xp) { this.xp = xp; return this; }
        public StudentDtoBuilder level(Integer level) { this.level = level; return this; }
        public StudentDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public StudentDto build() {
            return new StudentDto(id, userId, username, fullName, classroomId, classroomName, parentId, parentFullName, xp, level, createdAt, updatedAt);
        }
    }
}
