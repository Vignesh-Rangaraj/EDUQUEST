package com.eduquest.dto;

import com.eduquest.domain.ActivityStatus;
import com.eduquest.domain.DifficultyLevel;
import com.eduquest.domain.Module;
import com.eduquest.domain.Subject;

import java.time.LocalDateTime;

public class ModuleDto {

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

    public ModuleDto() {}

    public ModuleDto(Long id, String title, String description, Subject subject, DifficultyLevel difficultyLevel, Integer estimatedMinutes, Long classroomId, ActivityStatus status, Long createdByTeacherId, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public static ModuleDto fromEntity(Module m) {
        if (m == null) return null;
        return new ModuleDto(
                m.getId(),
                m.getTitle(),
                m.getDescription(),
                m.getSubject(),
                m.getDifficultyLevel(),
                m.getEstimatedMinutes(),
                m.getClassroomId(),
                m.getStatus(),
                m.getCreatedByTeacherId(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
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
}
