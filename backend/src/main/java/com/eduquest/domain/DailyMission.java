package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_missions")
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_key", nullable = false, unique = true)
    private String missionKey;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "target_count", nullable = false)
    private Integer targetCount;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    @Column(name = "coin_reward", nullable = false)
    private Integer coinReward;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DailyMission() {}

    public DailyMission(Long id, String missionKey, String title, String description, Integer targetCount, Integer xpReward, Integer coinReward, LocalDateTime createdAt) {
        this.id = id;
        this.missionKey = missionKey;
        this.title = title;
        this.description = description;
        this.targetCount = targetCount;
        this.xpReward = xpReward;
        this.coinReward = coinReward;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMissionKey() { return missionKey; }
    public void setMissionKey(String missionKey) { this.missionKey = missionKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTargetCount() { return targetCount; }
    public void setTargetCount(Integer targetCount) { this.targetCount = targetCount; }

    public Integer getXpReward() { return xpReward; }
    public void setXpReward(Integer xpReward) { this.xpReward = xpReward; }

    public Integer getCoinReward() { return coinReward; }
    public void setCoinReward(Integer coinReward) { this.coinReward = coinReward; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static DailyMissionBuilder builder() { return new DailyMissionBuilder(); }

    public static class DailyMissionBuilder {
        private Long id;
        private String missionKey;
        private String title;
        private String description;
        private Integer targetCount;
        private Integer xpReward;
        private Integer coinReward;
        private LocalDateTime createdAt;

        public DailyMissionBuilder id(Long id) { this.id = id; return this; }
        public DailyMissionBuilder missionKey(String missionKey) { this.missionKey = missionKey; return this; }
        public DailyMissionBuilder title(String title) { this.title = title; return this; }
        public DailyMissionBuilder description(String description) { this.description = description; return this; }
        public DailyMissionBuilder targetCount(Integer targetCount) { this.targetCount = targetCount; return this; }
        public DailyMissionBuilder xpReward(Integer xpReward) { this.xpReward = xpReward; return this; }
        public DailyMissionBuilder coinReward(Integer coinReward) { this.coinReward = coinReward; return this; }
        public DailyMissionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DailyMission build() {
            return new DailyMission(id, missionKey, title, description, targetCount, xpReward, coinReward, createdAt);
        }
    }
}
