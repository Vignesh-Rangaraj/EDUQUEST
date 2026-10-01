package com.eduquest.dto;

import java.util.List;

public class AdminAnalyticsDto {

    private long totalSchools;
    private long totalTeachers;
    private long totalStudents;
    private long totalModules;
    private long publishedModules;
    private long totalLessons;
    private long publishedLessons;
    private long totalQuizzes;
    private int activeRatePercentage;
    private List<ClassroomSummaryDto> topClassrooms;
    private List<StudentLeaderboardSummaryDto> topStudents;
    private List<TeacherSummaryDto> topTeachers;

    public AdminAnalyticsDto() {}

    public AdminAnalyticsDto(long totalSchools, long totalTeachers, long totalStudents, long totalModules, long publishedModules, long totalLessons, long publishedLessons, long totalQuizzes, int activeRatePercentage, List<ClassroomSummaryDto> topClassrooms, List<StudentLeaderboardSummaryDto> topStudents, List<TeacherSummaryDto> topTeachers) {
        this.totalSchools = totalSchools;
        this.totalTeachers = totalTeachers;
        this.totalStudents = totalStudents;
        this.totalModules = totalModules;
        this.publishedModules = publishedModules;
        this.totalLessons = totalLessons;
        this.publishedLessons = publishedLessons;
        this.totalQuizzes = totalQuizzes;
        this.activeRatePercentage = activeRatePercentage;
        this.topClassrooms = topClassrooms;
        this.topStudents = topStudents;
        this.topTeachers = topTeachers;
    }

    public long getTotalSchools() { return totalSchools; }
    public void setTotalSchools(long totalSchools) { this.totalSchools = totalSchools; }

    public long getTotalTeachers() { return totalTeachers; }
    public void setTotalTeachers(long totalTeachers) { this.totalTeachers = totalTeachers; }

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalModules() { return totalModules; }
    public void setTotalModules(long totalModules) { this.totalModules = totalModules; }

    public long getPublishedModules() { return publishedModules; }
    public void setPublishedModules(long publishedModules) { this.publishedModules = publishedModules; }

    public long getTotalLessons() { return totalLessons; }
    public void setTotalLessons(long totalLessons) { this.totalLessons = totalLessons; }

    public long getPublishedLessons() { return publishedLessons; }
    public void setPublishedLessons(long publishedLessons) { this.publishedLessons = publishedLessons; }

    public long getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(long totalQuizzes) { this.totalQuizzes = totalQuizzes; }

    public int getActiveRatePercentage() { return activeRatePercentage; }
    public void setActiveRatePercentage(int activeRatePercentage) { this.activeRatePercentage = activeRatePercentage; }

    public List<ClassroomSummaryDto> getTopClassrooms() { return topClassrooms; }
    public void setTopClassrooms(List<ClassroomSummaryDto> topClassrooms) { this.topClassrooms = topClassrooms; }

    public List<StudentLeaderboardSummaryDto> getTopStudents() { return topStudents; }
    public void setTopStudents(List<StudentLeaderboardSummaryDto> topStudents) { this.topStudents = topStudents; }

    public List<TeacherSummaryDto> getTopTeachers() { return topTeachers; }
    public void setTopTeachers(List<TeacherSummaryDto> topTeachers) { this.topTeachers = topTeachers; }

    public static class ClassroomSummaryDto {
        private Long id;
        private String name;
        private String grade;
        private int studentCount;
        private int averageXp;

        public ClassroomSummaryDto() {}
        public ClassroomSummaryDto(Long id, String name, String grade, int studentCount, int averageXp) {
            this.id = id;
            this.name = name;
            this.grade = grade;
            this.studentCount = studentCount;
            this.averageXp = averageXp;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public int getStudentCount() { return studentCount; }
        public void setStudentCount(int studentCount) { this.studentCount = studentCount; }
        public int getAverageXp() { return averageXp; }
        public void setAverageXp(int averageXp) { this.averageXp = averageXp; }
    }

    public static class StudentLeaderboardSummaryDto {
        private Long id;
        private String name;
        private String classroomName;
        private int xp;
        private int level;

        public StudentLeaderboardSummaryDto() {}
        public StudentLeaderboardSummaryDto(Long id, String name, String classroomName, int xp, int level) {
            this.id = id;
            this.name = name;
            this.classroomName = classroomName;
            this.xp = xp;
            this.level = level;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getClassroomName() { return classroomName; }
        public void setClassroomName(String classroomName) { this.classroomName = classroomName; }
        public int getXp() { return xp; }
        public void setXp(int xp) { this.xp = xp; }
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
    }

    public static class TeacherSummaryDto {
        private Long id;
        private String name;
        private String classroomName;
        private String subject;

        public TeacherSummaryDto() {}
        public TeacherSummaryDto(Long id, String name, String classroomName, String subject) {
            this.id = id;
            this.name = name;
            this.classroomName = classroomName;
            this.subject = subject;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getClassroomName() { return classroomName; }
        public void setClassroomName(String classroomName) { this.classroomName = classroomName; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
    }
}
