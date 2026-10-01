package com.eduquest.dto;

import java.util.List;

public class ParentChildActivityAnalyticsDto {
    private Long studentId;
    private String classroomName;
    private int activeActivities;
    private int completedActivities;
    private int pendingActivities;
    private int inProgressActivities;
    private int progressPercentage;
    private List<ParentSubjectActivityAnalyticsDto> subjects;
    private List<LeaderboardEntryDto> classroomLeaderboard;

    public ParentChildActivityAnalyticsDto() {}

    public ParentChildActivityAnalyticsDto(Long studentId, String classroomName, int activeActivities,
            int completedActivities, int pendingActivities, int inProgressActivities, int progressPercentage,
            List<ParentSubjectActivityAnalyticsDto> subjects, List<LeaderboardEntryDto> classroomLeaderboard) {
        this.studentId = studentId;
        this.classroomName = classroomName;
        this.activeActivities = activeActivities;
        this.completedActivities = completedActivities;
        this.pendingActivities = pendingActivities;
        this.inProgressActivities = inProgressActivities;
        this.progressPercentage = progressPercentage;
        this.subjects = subjects;
        this.classroomLeaderboard = classroomLeaderboard;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getClassroomName() { return classroomName; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }
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
    public List<ParentSubjectActivityAnalyticsDto> getSubjects() { return subjects; }
    public void setSubjects(List<ParentSubjectActivityAnalyticsDto> subjects) { this.subjects = subjects; }
    public List<LeaderboardEntryDto> getClassroomLeaderboard() { return classroomLeaderboard; }
    public void setClassroomLeaderboard(List<LeaderboardEntryDto> classroomLeaderboard) { this.classroomLeaderboard = classroomLeaderboard; }
}
