package com.eduquest.dto;

import com.eduquest.domain.StudentModuleProgress;

import java.time.LocalDateTime;

public class StudentModuleProgressDto {

    private Long id;
    private Long studentId;
    private Long moduleId;
    private String moduleTitle;
    private Integer completedActivities;
    private Integer totalActivities;
    private Double completionPercentage;
    private boolean completed;
    private LocalDateTime completedAt;

    public StudentModuleProgressDto() {}

    public StudentModuleProgressDto(Long id, Long studentId, Long moduleId, String moduleTitle, Integer completedActivities, Integer totalActivities, Double completionPercentage, boolean completed, LocalDateTime completedAt) {
        this.id = id;
        this.studentId = studentId;
        this.moduleId = moduleId;
        this.moduleTitle = moduleTitle;
        this.completedActivities = completedActivities;
        this.totalActivities = totalActivities;
        this.completionPercentage = completionPercentage;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public static StudentModuleProgressDto fromEntity(StudentModuleProgress smp, String moduleTitle) {
        if (smp == null) return null;
        return new StudentModuleProgressDto(
                smp.getId(),
                smp.getStudentId(),
                smp.getModuleId(),
                moduleTitle,
                smp.getCompletedActivities(),
                smp.getTotalActivities(),
                smp.getCompletionPercentage(),
                smp.isCompleted(),
                smp.getCompletedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getModuleTitle() { return moduleTitle; }
    public void setModuleTitle(String moduleTitle) { this.moduleTitle = moduleTitle; }

    public Integer getCompletedActivities() { return completedActivities; }
    public void setCompletedActivities(Integer completedActivities) { this.completedActivities = completedActivities; }

    public Integer getTotalActivities() { return totalActivities; }
    public void setTotalActivities(Integer totalActivities) { this.totalActivities = totalActivities; }

    public Double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(Double completionPercentage) { this.completionPercentage = completionPercentage; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
