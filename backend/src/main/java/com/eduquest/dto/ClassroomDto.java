package com.eduquest.dto;

import java.time.LocalDateTime;

public class ClassroomDto {
    private Long id;
    private Integer grade;
    private String section;
    private String name;
    private String schoolName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClassroomDto() {}

    public ClassroomDto(Long id, Integer grade, String section, String name, String schoolName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.grade = grade;
        this.section = section;
        this.name = name;
        this.schoolName = schoolName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ClassroomDtoBuilder builder() {
        return new ClassroomDtoBuilder();
    }

    public static class ClassroomDtoBuilder {
        private Long id;
        private Integer grade;
        private String section;
        private String name;
        private String schoolName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ClassroomDtoBuilder id(Long id) { this.id = id; return this; }
        public ClassroomDtoBuilder grade(Integer grade) { this.grade = grade; return this; }
        public ClassroomDtoBuilder section(String section) { this.section = section; return this; }
        public ClassroomDtoBuilder name(String name) { this.name = name; return this; }
        public ClassroomDtoBuilder schoolName(String schoolName) { this.schoolName = schoolName; return this; }
        public ClassroomDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ClassroomDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ClassroomDto build() {
            return new ClassroomDto(id, grade, section, name, schoolName, createdAt, updatedAt);
        }
    }
}
