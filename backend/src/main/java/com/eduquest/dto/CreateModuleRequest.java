package com.eduquest.dto;

import com.eduquest.domain.DifficultyLevel;
import com.eduquest.domain.Subject;

public class CreateModuleRequest {

    private String title;
    private String description;
    private Subject subject;
    private DifficultyLevel difficultyLevel;
    private Integer estimatedMinutes;
    private Long classroomId;

    public CreateModuleRequest() {}

    public CreateModuleRequest(String title, String description, Subject subject, DifficultyLevel difficultyLevel, Integer estimatedMinutes, Long classroomId) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.difficultyLevel = difficultyLevel;
        this.estimatedMinutes = estimatedMinutes;
        this.classroomId = classroomId;
    }

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
}
