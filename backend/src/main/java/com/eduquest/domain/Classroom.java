package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Classroom() {}

    public Classroom(Long id, Integer grade, String section, String name, School school, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.grade = grade;
        this.section = section;
        this.name = name;
        this.school = school;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ClassroomBuilder builder() {
        return new ClassroomBuilder();
    }

    public static class ClassroomBuilder {
        private Long id;
        private Integer grade;
        private String section;
        private String name;
        private School school;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ClassroomBuilder id(Long id) { this.id = id; return this; }
        public ClassroomBuilder grade(Integer grade) { this.grade = grade; return this; }
        public ClassroomBuilder section(String section) { this.section = section; return this; }
        public ClassroomBuilder name(String name) { this.name = name; return this; }
        public ClassroomBuilder school(School school) { this.school = school; return this; }
        public ClassroomBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ClassroomBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Classroom build() {
            return new Classroom(id, grade, section, name, school, createdAt, updatedAt);
        }
    }
}
