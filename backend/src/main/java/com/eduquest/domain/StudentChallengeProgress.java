package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_challenge_progress",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_challenge", columnNames = {"challenge_id", "student_id"})
    }
)
public class StudentChallengeProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "challenge_id", nullable = false)
    private Long challengeId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "current_progress", nullable = false)
    private Integer currentProgress = 0;

    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    @Column(name = "claimed", nullable = false)
    private Boolean claimed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StudentChallengeProgress() {}

    public StudentChallengeProgress(Long id, Long challengeId, Long studentId, Integer currentProgress, Boolean completed, Boolean claimed, LocalDateTime completedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.challengeId = challengeId;
        this.studentId = studentId;
        this.currentProgress = currentProgress != null ? currentProgress : 0;
        this.completed = completed != null ? completed : false;
        this.claimed = claimed != null ? claimed : false;
        this.completedAt = completedAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.updatedAt = now;
        if (this.currentProgress == null) this.currentProgress = 0;
        if (this.completed == null) this.completed = false;
        if (this.claimed == null) this.claimed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Integer getCurrentProgress() { return currentProgress != null ? currentProgress : 0; }
    public void setCurrentProgress(Integer currentProgress) { this.currentProgress = currentProgress; }

    public Boolean getCompleted() { return completed != null ? completed : false; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public Boolean getClaimed() { return claimed != null ? claimed : false; }
    public void setClaimed(Boolean claimed) { this.claimed = claimed; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static StudentChallengeProgressBuilder builder() { return new StudentChallengeProgressBuilder(); }

    public static class StudentChallengeProgressBuilder {
        private Long id;
        private Long challengeId;
        private Long studentId;
        private Integer currentProgress = 0;
        private Boolean completed = false;
        private Boolean claimed = false;
        private LocalDateTime completedAt;
        private LocalDateTime updatedAt;

        public StudentChallengeProgressBuilder id(Long id) { this.id = id; return this; }
        public StudentChallengeProgressBuilder challengeId(Long challengeId) { this.challengeId = challengeId; return this; }
        public StudentChallengeProgressBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentChallengeProgressBuilder currentProgress(Integer currentProgress) { this.currentProgress = currentProgress; return this; }
        public StudentChallengeProgressBuilder completed(Boolean completed) { this.completed = completed; return this; }
        public StudentChallengeProgressBuilder claimed(Boolean claimed) { this.claimed = claimed; return this; }
        public StudentChallengeProgressBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public StudentChallengeProgressBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public StudentChallengeProgress build() {
            return new StudentChallengeProgress(id, challengeId, studentId, currentProgress, completed, claimed, completedAt, updatedAt);
        }
    }
}
