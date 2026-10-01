package com.eduquest.dto;

import java.time.LocalDateTime;

public class AchievementDto {

    private String badgeCode;
    private String badgeName;
    private String description;
    private String icon;
    private boolean earned;
    private LocalDateTime earnedAt;

    public AchievementDto() {}

    public AchievementDto(String badgeCode, String badgeName, String description, String icon, boolean earned, LocalDateTime earnedAt) {
        this.badgeCode = badgeCode;
        this.badgeName = badgeName;
        this.description = description;
        this.icon = icon;
        this.earned = earned;
        this.earnedAt = earnedAt;
    }

    public String getBadgeCode() { return badgeCode; }
    public void setBadgeCode(String badgeCode) { this.badgeCode = badgeCode; }

    public String getBadgeName() { return badgeName; }
    public void setBadgeName(String badgeName) { this.badgeName = badgeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isEarned() { return earned; }
    public void setEarned(boolean earned) { this.earned = earned; }

    public LocalDateTime getEarnedAt() { return earnedAt; }
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; }
}
