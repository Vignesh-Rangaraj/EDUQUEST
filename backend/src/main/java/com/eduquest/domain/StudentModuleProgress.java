package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_module_progress",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_module", columnNames = {"student_id", "module_id"})
    }
)
public class StudentModuleProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "completed_activities", nullable = false)
    private Integer completedActivities = 0;

    @Column(name = "total_activities", nullable = false)
    private Integer totalActivities = 0;

    @Column(name = "completion_percentage", nullable = false)
    private Double completionPercentage = 0.0;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StudentModuleProgress() {}

    public StudentModuleProgress(Long id, Long studentId, Long moduleId, Integer completedActivities, Integer totalActivities, Double completionPercentage, boolean completed, LocalDateTime completedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.moduleId = moduleId;
        this.completedActivities = completedActivities != null ? completedActivities : 0;
        this.totalActivities = totalActivities != null ? totalActivities : 0;
        this.completionPercentage = completionPercentage != null ? completionPercentage : 0.0;
        this.completed = completed;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.completedActivities == null) this.completedActivities = 0;
        if (this.totalActivities == null) this.totalActivities = 0;
        if (this.completionPercentage == null) this.completionPercentage = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static StudentModuleProgressBuilder builder() { return new StudentModuleProgressBuilder(); }

    public static class StudentModuleProgressBuilder {
        private Long id;
        private Long studentId;
        private Long moduleId;
        private Integer completedActivities = 0;
        private Integer totalActivities = 0;
        private Double completionPercentage = 0.0;
        private boolean completed = false;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public StudentModuleProgressBuilder id(Long id) { this.id = id; return this; }
        public StudentModuleProgressBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentModuleProgressBuilder moduleId(Long moduleId) { this.moduleId = moduleId; return this; }
        public StudentModuleProgressBuilder completedActivities(Integer completedActivities) { this.completedActivities = completedActivities; return this; }
        public StudentModuleProgressBuilder totalActivities(Integer totalActivities) { this.totalActivities = totalActivities; return this; }
        public StudentModuleProgressBuilder completionPercentage(Double completionPercentage) { this.completionPercentage = completionPercentage; return this; }
        public StudentModuleProgressBuilder completed(boolean completed) { this.completed = completed; return this; }
        public StudentModuleProgressBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public StudentModuleProgressBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentModuleProgressBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public StudentModuleProgress build() {
            return new StudentModuleProgress(id, studentId, moduleId, completedActivities, totalActivities, completionPercentage, completed, completedAt, createdAt, updatedAt);
        }
    }
}
