package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_missions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_mission_date", columnNames = {"student_id", "mission_key", "mission_date"})
    }
)
public class StudentMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "mission_key", nullable = false)
    private String missionKey;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "current_progress", nullable = false)
    private Integer currentProgress = 0;

    @Column(name = "target_count", nullable = false)
    private Integer targetCount;

    @Column(name = "completed", nullable = false)
    private Boolean completed = false;

    @Column(name = "claimed", nullable = false)
    private Boolean claimed = false;

    @Column(name = "mission_date", nullable = false)
    private LocalDate missionDate;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    @Column(name = "coin_reward", nullable = false)
    private Integer coinReward;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StudentMission() {}

    public StudentMission(Long id, Long studentId, String missionKey, String title, String description, Integer currentProgress, Integer targetCount, Boolean completed, Boolean claimed, LocalDate missionDate, Integer xpReward, Integer coinReward, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.missionKey = missionKey;
        this.title = title;
        this.description = description;
        this.currentProgress = currentProgress != null ? currentProgress : 0;
        this.targetCount = targetCount;
        this.completed = completed != null ? completed : false;
        this.claimed = claimed != null ? claimed : false;
        this.missionDate = missionDate;
        this.xpReward = xpReward;
        this.coinReward = coinReward;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.currentProgress == null) this.currentProgress = 0;
        if (this.completed == null) this.completed = false;
        if (this.claimed == null) this.claimed = false;
        if (this.missionDate == null) this.missionDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getMissionKey() { return missionKey; }
    public void setMissionKey(String missionKey) { this.missionKey = missionKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCurrentProgress() { return currentProgress != null ? currentProgress : 0; }
    public void setCurrentProgress(Integer currentProgress) { this.currentProgress = currentProgress; }

    public Integer getTargetCount() { return targetCount; }
    public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }

    public Boolean getCompleted() { return completed != null ? completed : false; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public Boolean getClaimed() { return claimed != null ? claimed : false; }
    public void setClaimed(Boolean claimed) { this.claimed = claimed; }

    public LocalDate getMissionDate() { return missionDate; }
    public void setMissionDate(LocalDate missionDate) { this.missionDate = missionDate; }

    public Integer getXpReward() { return xpReward; }
    public void setXpReward(Integer xpReward) { this.xpReward = xpReward; }

    public Integer getCoinReward() { return coinReward; }
    public void setCoinReward(Integer coinReward) { this.coinReward = coinReward; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static StudentMissionBuilder builder() { return new StudentMissionBuilder(); }

    public static class StudentMissionBuilder {
        private Long id;
        private Long studentId;
        private String missionKey;
        private String title;
        private String description;
        private Integer currentProgress = 0;
        private Integer targetCount;
        private Boolean completed = false;
        private Boolean claimed = false;
        private LocalDate missionDate;
        private Integer xpReward;
        private Integer coinReward;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public StudentMissionBuilder id(Long id) { this.id = id; return this; }
        public StudentMissionBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentMissionBuilder missionKey(String missionKey) { this.missionKey = missionKey; return this; }
        public StudentMissionBuilder title(String title) { this.title = title; return this; }
        public StudentMissionBuilder description(String description) { this.description = description; return this; }
        public StudentMissionBuilder currentProgress(Integer currentProgress) { this.currentProgress = currentProgress; return this; }
        public StudentMissionBuilder targetCount(Integer targetCount) { this.targetCount = targetCount; return this; }
        public StudentMissionBuilder completed(Boolean completed) { this.completed = completed; return this; }
        public StudentMissionBuilder claimed(Boolean claimed) { this.claimed = claimed; return this; }
        public StudentMissionBuilder missionDate(LocalDate missionDate) { this.missionDate = missionDate; return this; }
        public StudentMissionBuilder xpReward(Integer xpReward) { this.xpReward = xpReward; return this; }
        public StudentMissionBuilder coinReward(Integer coinReward) { this.coinReward = coinReward; return this; }
        public StudentMissionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentMissionBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public StudentMission build() {
            return new StudentMission(id, studentId, missionKey, title, description, currentProgress, targetCount, completed, claimed, missionDate, xpReward, coinReward, createdAt, updatedAt);
        }
    }
}
