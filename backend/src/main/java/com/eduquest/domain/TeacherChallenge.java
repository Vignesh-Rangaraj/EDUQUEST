package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacher_challenges")
public class TeacherChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "classroom_id", nullable = false)
    private Long classroomId;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "target_type", nullable = false)
    private String targetType; // LESSONS_COMPLETED, QUIZ_SCORE, XP_EARNED, STREAK_DAYS

    @Column(name = "target_value", nullable = false)
    private Integer targetValue;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    @Column(name = "coin_reward", nullable = false)
    private Integer coinReward;

    @Column(name = "badge_reward_code")
    private String badgeRewardCode;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "archived", nullable = false)
    private Boolean archived = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TeacherChallenge() {}

    public TeacherChallenge(Long id, Long classroomId, Long teacherId, String title, String description, String targetType, Integer targetValue, Integer xpReward, Integer coinReward, String badgeRewardCode, LocalDate startDate, LocalDate endDate, Boolean archived, LocalDateTime createdAt) {
        this.id = id;
        this.classroomId = classroomId;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.targetType = targetType;
        this.targetValue = targetValue;
        this.xpReward = xpReward;
        this.coinReward = coinReward;
        this.badgeRewardCode = badgeRewardCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.archived = archived != null ? archived : false;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.archived == null) {
            this.archived = false;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }

    public Integer getXpReward() { return xpReward; }
    public void setXpReward(Integer xpReward) { this.xpReward = xpReward; }

    public Integer getCoinReward() { return coinReward; }
    public void setCoinReward(Integer coinReward) { this.coinReward = coinReward; }

    public String getBadgeRewardCode() { return badgeRewardCode; }
    public void setBadgeRewardCode(String badgeRewardCode) { this.badgeRewardCode = badgeRewardCode; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getArchived() { return archived != null ? archived : false; }
    public void setArchived(Boolean archived) { this.archived = archived; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static TeacherChallengeBuilder builder() { return new TeacherChallengeBuilder(); }

    public static class TeacherChallengeBuilder {
        private Long id;
        private Long classroomId;
        private Long teacherId;
        private String title;
        private String description;
        private String targetType;
        private Integer targetValue;
        private Integer xpReward;
        private Integer coinReward;
        private String badgeRewardCode;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean archived = false;
        private LocalDateTime createdAt;

        public TeacherChallengeBuilder id(Long id) { this.id = id; return this; }
        public TeacherChallengeBuilder classroomId(Long classroomId) { this.classroomId = classroomId; return this; }
        public TeacherChallengeBuilder teacherId(Long teacherId) { this.teacherId = teacherId; return this; }
        public TeacherChallengeBuilder title(String title) { this.title = title; return this; }
        public TeacherChallengeBuilder description(String description) { this.description = description; return this; }
        public TeacherChallengeBuilder targetType(String targetType) { this.targetType = targetType; return this; }
        public TeacherChallengeBuilder targetValue(Integer targetValue) { this.targetValue = targetValue; return this; }
        public TeacherChallengeBuilder xpReward(Integer xpReward) { this.xpReward = xpReward; return this; }
        public TeacherChallengeBuilder coinReward(Integer coinReward) { this.coinReward = coinReward; return this; }
        public TeacherChallengeBuilder badgeRewardCode(String badgeRewardCode) { this.badgeRewardCode = badgeRewardCode; return this; }
        public TeacherChallengeBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public TeacherChallengeBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public TeacherChallengeBuilder archived(Boolean archived) { this.archived = archived; return this; }
        public TeacherChallengeBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TeacherChallenge build() {
            return new TeacherChallenge(id, classroomId, teacherId, title, description, targetType, targetValue, xpReward, coinReward, badgeRewardCode, startDate, endDate, archived, createdAt);
        }
    }
}
