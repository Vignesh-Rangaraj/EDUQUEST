package com.eduquest.dto;

import java.util.List;

public class TeacherAnalyticsDto {

    private Long classroomId;
    private String classroomName;
    private int totalStudents;
    private int averageXp;
    private int averageCompletionRate;
    private int activeStudentsCount;
    private List<StudentPerformanceSummary> studentPerformanceList;
    private List<StudentPerformanceSummary> studentsNeedingAttention;
    private List<SubjectProgressDto> subjectAnalytics;

    // Phase 7.2 Detailed Analytics Fields
    private int totalPublishedActivities;
    private int totalDraftActivities;
    private int totalGamesCount;
    private int totalLessonsCount;
    private int totalQuizzesCount;
    private int totalAttempts;
    private int averageXpEarned;
    private int averageCoinsEarned;
    private String mostPlayedGameType;
    private String leastPlayedActivityTitle;
    private String highestPerformingStudentName;
    private String lowestCompletionActivityTitle;

    public TeacherAnalyticsDto() {}

    public TeacherAnalyticsDto(Long classroomId, String classroomName, int totalStudents, int averageXp, int averageCompletionRate, int activeStudentsCount, List<StudentPerformanceSummary> studentPerformanceList, List<StudentPerformanceSummary> studentsNeedingAttention, List<SubjectProgressDto> subjectAnalytics) {
        this.classroomId = classroomId;
        this.classroomName = classroomName;
        this.totalStudents = totalStudents;
        this.averageXp = averageXp;
        this.averageCompletionRate = averageCompletionRate;
        this.activeStudentsCount = activeStudentsCount;
        this.studentPerformanceList = studentPerformanceList;
        this.studentsNeedingAttention = studentsNeedingAttention;
        this.subjectAnalytics = subjectAnalytics;
    }

    public int getTotalPublishedActivities() { return totalPublishedActivities; }
    public void setTotalPublishedActivities(int totalPublishedActivities) { this.totalPublishedActivities = totalPublishedActivities; }

    public int getTotalDraftActivities() { return totalDraftActivities; }
    public void setTotalDraftActivities(int totalDraftActivities) { this.totalDraftActivities = totalDraftActivities; }

    public int getTotalGamesCount() { return totalGamesCount; }
    public void setTotalGamesCount(int totalGamesCount) { this.totalGamesCount = totalGamesCount; }

    public int getTotalLessonsCount() { return totalLessonsCount; }
    public void setTotalLessonsCount(int totalLessonsCount) { this.totalLessonsCount = totalLessonsCount; }

    public int getTotalQuizzesCount() { return totalQuizzesCount; }
    public void setTotalQuizzesCount(int totalQuizzesCount) { this.totalQuizzesCount = totalQuizzesCount; }

    public int getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(int totalAttempts) { this.totalAttempts = totalAttempts; }

    public int getAverageXpEarned() { return averageXpEarned; }
    public void setAverageXpEarned(int averageXpEarned) { this.averageXpEarned = averageXpEarned; }

    public int getAverageCoinsEarned() { return averageCoinsEarned; }
    public void setAverageCoinsEarned(int averageCoinsEarned) { this.averageCoinsEarned = averageCoinsEarned; }

    public String getMostPlayedGameType() { return mostPlayedGameType; }
    public void setMostPlayedGameType(String mostPlayedGameType) { this.mostPlayedGameType = mostPlayedGameType; }

    public String getLeastPlayedActivityTitle() { return leastPlayedActivityTitle; }
    public void setLeastPlayedActivityTitle(String leastPlayedActivityTitle) { this.leastPlayedActivityTitle = leastPlayedActivityTitle; }

    public String getHighestPerformingStudentName() { return highestPerformingStudentName; }
    public void setHighestPerformingStudentName(String highestPerformingStudentName) { this.highestPerformingStudentName = highestPerformingStudentName; }

    public String getLowestCompletionActivityTitle() { return lowestCompletionActivityTitle; }
    public void setLowestCompletionActivityTitle(String lowestCompletionActivityTitle) { this.lowestCompletionActivityTitle = lowestCompletionActivityTitle; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

    public String getClassroomName() { return classroomName; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public int getAverageXp() { return averageXp; }
    public void setAverageXp(int averageXp) { this.averageXp = averageXp; }

    public int getAverageCompletionRate() { return averageCompletionRate; }
    public void setAverageCompletionRate(int averageCompletionRate) { this.averageCompletionRate = averageCompletionRate; }

    public int getActiveStudentsCount() { return activeStudentsCount; }
    public void setActiveStudentsCount(int activeStudentsCount) { this.activeStudentsCount = activeStudentsCount; }

    public List<StudentPerformanceSummary> getStudentPerformanceList() { return studentPerformanceList; }
    public void setStudentPerformanceList(List<StudentPerformanceSummary> studentPerformanceList) { this.studentPerformanceList = studentPerformanceList; }

    public List<StudentPerformanceSummary> getStudentsNeedingAttention() { return studentsNeedingAttention; }
    public void setStudentsNeedingAttention(List<StudentPerformanceSummary> studentsNeedingAttention) { this.studentsNeedingAttention = studentsNeedingAttention; }

    public List<SubjectProgressDto> getSubjectAnalytics() { return subjectAnalytics; }
    public void setSubjectAnalytics(List<SubjectProgressDto> subjectAnalytics) { this.subjectAnalytics = subjectAnalytics; }

    public static class StudentPerformanceSummary {
        private Long studentId;
        private String studentName;
        private int totalXp;
        private int level;
        private int rank;
        private int completedLessons;
        private int completedQuizzes;
        private int completionPercentage;
        private String status; // ACTIVE, NEEDS_ATTENTION, INACTIVE

        public StudentPerformanceSummary() {}

        public StudentPerformanceSummary(Long studentId, String studentName, int totalXp, int level, int rank, int completedLessons, int completedQuizzes, int completionPercentage, String status) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.totalXp = totalXp;
            this.level = level;
            this.rank = rank;
            this.completedLessons = completedLessons;
            this.completedQuizzes = completedQuizzes;
            this.completionPercentage = completionPercentage;
            this.status = status;
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

        public int getCompletedLessons() { return completedLessons; }
        public void setCompletedLessons(int completedLessons) { this.completedLessons = completedLessons; }

        public int getCompletedQuizzes() { return completedQuizzes; }
        public void setCompletedQuizzes(int completedQuizzes) { this.completedQuizzes = completedQuizzes; }

        public int getCompletionPercentage() { return completionPercentage; }
        public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
