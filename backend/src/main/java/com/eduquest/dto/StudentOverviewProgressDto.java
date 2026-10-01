package com.eduquest.dto;

import java.util.List;

public class StudentOverviewProgressDto {

    private Long studentId;
    private String studentName;
    private int totalXp;
    private int level;
    private int rank;
    private int streakDays;
    private int completedLessons;
    private int completedQuizzes;
    private int overallProgressPercentage;
    private List<SubjectProgressDto> subjectProgresses;
    private List<StudentProgressDto> recentActivities;
    private List<AchievementDto> achievements;

    public StudentOverviewProgressDto() {}

    public StudentOverviewProgressDto(Long studentId, String studentName, int totalXp, int level, int rank, int streakDays, int completedLessons, int completedQuizzes, int overallProgressPercentage, List<SubjectProgressDto> subjectProgresses, List<StudentProgressDto> recentActivities, List<AchievementDto> achievements) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalXp = totalXp;
        this.level = level;
        this.rank = rank;
        this.streakDays = streakDays;
        this.completedLessons = completedLessons;
        this.completedQuizzes = completedQuizzes;
        this.overallProgressPercentage = overallProgressPercentage;
        this.subjectProgresses = subjectProgresses;
        this.recentActivities = recentActivities;
        this.achievements = achievements;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }

    public int getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(int completedLessons) { this.completedLessons = completedLessons; }

    public int getCompletedQuizzes() { return completedQuizzes; }
    public void setCompletedQuizzes(int completedQuizzes) { this.completedQuizzes = completedQuizzes; }

    public int getOverallProgressPercentage() { return overallProgressPercentage; }
    public void setOverallProgressPercentage(int overallProgressPercentage) { this.overallProgressPercentage = overallProgressPercentage; }

    public List<SubjectProgressDto> getSubjectProgresses() { return subjectProgresses; }
    public void setSubjectProgresses(List<SubjectProgressDto> subjectProgresses) { this.subjectProgresses = subjectProgresses; }

    public List<StudentProgressDto> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<StudentProgressDto> recentActivities) { this.recentActivities = recentActivities; }

    public List<AchievementDto> getAchievements() { return achievements; }
    public void setAchievements(List<AchievementDto> achievements) { this.achievements = achievements; }
}
