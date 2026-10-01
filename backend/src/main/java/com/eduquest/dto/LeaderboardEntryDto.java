package com.eduquest.dto;

public class LeaderboardEntryDto {

    private int rank;
    private Long studentId;
    private String studentName;
    private String classroomName;
    private int xp;
    private int level;

    public LeaderboardEntryDto() {}

    public LeaderboardEntryDto(int rank, Long studentId, String studentName, String classroomName, int xp, int level) {
        this.rank = rank;
        this.studentId = studentId;
        this.studentName = studentName;
        this.classroomName = classroomName;
        this.xp = xp;
        this.level = level;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClassroomName() { return classroomName; }
    public void setClassroomName(String classroomName) { this.classroomName = classroomName; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}
