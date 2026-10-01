package com.eduquest.dto;

public class ParentSubjectActivityAnalyticsDto {
    private String subject;
    private int activeActivities;
    private int completedActivities;
    private int pendingActivities;
    private int inProgressActivities;
    private int progressPercentage;

    public ParentSubjectActivityAnalyticsDto() {}

    public ParentSubjectActivityAnalyticsDto(String subject, int activeActivities, int completedActivities,
            int pendingActivities, int inProgressActivities, int progressPercentage) {
        this.subject = subject;
        this.activeActivities = activeActivities;
        this.completedActivities = completedActivities;
        this.pendingActivities = pendingActivities;
        this.inProgressActivities = inProgressActivities;
        this.progressPercentage = progressPercentage;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public int getActiveActivities() { return activeActivities; }
    public void setActiveActivities(int activeActivities) { this.activeActivities = activeActivities; }
    public int getCompletedActivities() { return completedActivities; }
    public void setCompletedActivities(int completedActivities) { this.completedActivities = completedActivities; }
    public int getPendingActivities() { return pendingActivities; }
    public void setPendingActivities(int pendingActivities) { this.pendingActivities = pendingActivities; }
    public int getInProgressActivities() { return inProgressActivities; }
    public void setInProgressActivities(int inProgressActivities) { this.inProgressActivities = inProgressActivities; }
    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }
}
