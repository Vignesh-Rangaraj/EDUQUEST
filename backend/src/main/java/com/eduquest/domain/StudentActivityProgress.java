package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_activity_progress",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_activity_progress", columnNames = {"student_id", "activity_id"})
    }
)
public class StudentActivityProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "best_score", nullable = false)
    private Integer bestScore = 0;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 1;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StudentActivityProgress() {}

    public StudentActivityProgress(Long id, Long studentId, Long activityId, boolean completed, Integer score, Integer bestScore, Integer attemptCount, LocalDateTime completedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.activityId = activityId;
        this.completed = completed;
        this.score = score;
        this.bestScore = bestScore != null ? bestScore : (score != null ? score : 0);
        this.attemptCount = attemptCount != null ? attemptCount : 1;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.attemptCount == null) this.attemptCount = 1;
        if (this.bestScore == null) this.bestScore = this.score != null ? this.score : 0;
        if (this.completedAt == null && this.completed) {
            this.completedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getBestScore() { return bestScore; }
    public void setBestScore(Integer bestScore) { this.bestScore = bestScore; }

    public Integer getAttemptCount() { return attemptCount; }
    public void setAttemptCount(Integer attemptCount) { this.attemptCount = attemptCount; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static StudentActivityProgressBuilder builder() { return new StudentActivityProgressBuilder(); }

    public static class StudentActivityProgressBuilder {
        private Long id;
        private Long studentId;
        private Long activityId;
        private boolean completed;
        private Integer score;
        private Integer bestScore;
        private Integer attemptCount;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public StudentActivityProgressBuilder id(Long id) { this.id = id; return this; }
        public StudentActivityProgressBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentActivityProgressBuilder activityId(Long activityId) { this.activityId = activityId; return this; }
        public StudentActivityProgressBuilder completed(boolean completed) { this.completed = completed; return this; }
        public StudentActivityProgressBuilder score(Integer score) { this.score = score; return this; }
        public StudentActivityProgressBuilder bestScore(Integer bestScore) { this.bestScore = bestScore; return this; }
        public StudentActivityProgressBuilder attemptCount(Integer attemptCount) { this.attemptCount = attemptCount; return this; }
        public StudentActivityProgressBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public StudentActivityProgressBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentActivityProgressBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public StudentActivityProgress build() {
            return new StudentActivityProgress(id, studentId, activityId, completed, score, bestScore, attemptCount, completedAt, createdAt, updatedAt);
        }
    }
}
