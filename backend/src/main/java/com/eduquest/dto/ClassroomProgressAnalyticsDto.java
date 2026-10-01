package com.eduquest.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ClassroomProgressAnalyticsDto {
    private Long classroomId;
    private String classroomName;
    private int studentCount;
    private int availableActivityCount;
    private int completedAssignments;
    private int pendingAssignments;
    private int classroomCompletionPercentage;
    private List<StudentProgressSummary> students;

    public ClassroomProgressAnalyticsDto() {}

    public ClassroomProgressAnalyticsDto(Long classroomId, String classroomName, int studentCount,
            int availableActivityCount, int completedAssignments, int pendingAssignments,
            int classroomCompletionPercentage, List<StudentProgressSummary> students) {
        this.classroomId = classroomId;
        this.classroomName = classroomName;
        this.studentCount = studentCount;
        this.availableActivityCount = availableActivityCount;
        this.completedAssignments = completedAssignments;
        this.pendingAssignments = pendingAssignments;
        this.classroomCompletionPercentage = classroomCompletionPercentage;
        this.students = students;
    }

    public Long getClassroomId() { return classroomId; }
    public String getClassroomName() { return classroomName; }
    public int getStudentCount() { return studentCount; }
    public int getAvailableActivityCount() { return availableActivityCount; }
    public int getCompletedAssignments() { return completedAssignments; }
    public int getPendingAssignments() { return pendingAssignments; }
    public int getClassroomCompletionPercentage() { return classroomCompletionPercentage; }
    public List<StudentProgressSummary> getStudents() { return students; }

    public static class StudentProgressSummary {
        private Long studentId;
        private String fullName;
        private String username;
        private int xp;
        private int level;
        private int completedCount;
        private int pendingCount;
        private int totalActivities;
        private int completionPercentage;
        private LocalDateTime lastActivityAt;

        public StudentProgressSummary() {}

        public StudentProgressSummary(Long studentId, String fullName, String username, int xp, int level,
                int completedCount, int pendingCount, int totalActivities, int completionPercentage,
                LocalDateTime lastActivityAt) {
            this.studentId = studentId;
            this.fullName = fullName;
            this.username = username;
            this.xp = xp;
            this.level = level;
            this.completedCount = completedCount;
            this.pendingCount = pendingCount;
            this.totalActivities = totalActivities;
            this.completionPercentage = completionPercentage;
            this.lastActivityAt = lastActivityAt;
        }

        public Long getStudentId() { return studentId; }
        public String getFullName() { return fullName; }
        public String getUsername() { return username; }
        public int getXp() { return xp; }
        public int getLevel() { return level; }
        public int getCompletedCount() { return completedCount; }
        public int getPendingCount() { return pendingCount; }
        public int getTotalActivities() { return totalActivities; }
        public int getCompletionPercentage() { return completionPercentage; }
        public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    }
}
