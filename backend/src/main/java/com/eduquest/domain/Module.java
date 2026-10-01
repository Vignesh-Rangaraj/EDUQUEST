package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "classroom_id")
    private Long classroomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    @Column(name = "created_by_teacher_id")
    private Long createdByTeacherId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Module() {}

    public Module(Long id, String title, String description, Subject subject, DifficultyLevel difficultyLevel, Integer estimatedMinutes, Long classroomId, ActivityStatus status, Long createdByTeacherId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.difficultyLevel = difficultyLevel;
        this.estimatedMinutes = estimatedMinutes;
        this.classroomId = classroomId;
        this.status = status;
        this.createdByTeacherId = createdByTeacherId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ActivityStatus.DRAFT;
        }
        if (this.difficultyLevel == null) {
            this.difficultyLevel = DifficultyLevel.BEGINNER;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

    public ActivityStatus getStatus() { return status; }
    public void setStatus(ActivityStatus status) { this.status = status; }

    public Long getCreatedByTeacherId() { return createdByTeacherId; }
    public void setCreatedByTeacherId(Long createdByTeacherId) { this.createdByTeacherId = createdByTeacherId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ModuleBuilder builder() { return new ModuleBuilder(); }

    public static class ModuleBuilder {
        private Long id;
        private String title;
        private String description;
        private Subject subject;
        private DifficultyLevel difficultyLevel;
        private Integer estimatedMinutes;
        private Long classroomId;
        private ActivityStatus status;
        private Long createdByTeacherId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ModuleBuilder id(Long id) { this.id = id; return this; }
        public ModuleBuilder title(String title) { this.title = title; return this; }
        public ModuleBuilder description(String description) { this.description = description; return this; }
        public ModuleBuilder subject(Subject subject) { this.subject = subject; return this; }
        public ModuleBuilder difficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; return this; }
        public ModuleBuilder estimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; return this; }
        public ModuleBuilder classroomId(Long classroomId) { this.classroomId = classroomId; return this; }
        public ModuleBuilder status(ActivityStatus status) { this.status = status; return this; }
        public ModuleBuilder createdByTeacherId(Long createdByTeacherId) { this.createdByTeacherId = createdByTeacherId; return this; }
        public ModuleBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ModuleBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Module build() {
            return new Module(id, title, description, subject, difficultyLevel, estimatedMinutes, classroomId, status, createdByTeacherId, createdAt, updatedAt);
        }
    }
}
