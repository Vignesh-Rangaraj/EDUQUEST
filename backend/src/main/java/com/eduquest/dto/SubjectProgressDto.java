package com.eduquest.dto;

public class SubjectProgressDto {

    private String subject;
    private int completedLessons;
    private int totalLessons;
    private int completedQuizzes;
    private int totalQuizzes;
    private int progressPercentage;

    public SubjectProgressDto() {}

    public SubjectProgressDto(String subject, int completedLessons, int totalLessons, int completedQuizzes, int totalQuizzes, int progressPercentage) {
        this.subject = subject;
        this.completedLessons = completedLessons;
        this.totalLessons = totalLessons;
        this.completedQuizzes = completedQuizzes;
        this.totalQuizzes = totalQuizzes;
        this.progressPercentage = progressPercentage;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(int completedLessons) { this.completedLessons = completedLessons; }

    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }

    public int getCompletedQuizzes() { return completedQuizzes; }
    public void setCompletedQuizzes(int completedQuizzes) { this.completedQuizzes = completedQuizzes; }

    public int getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }
}
