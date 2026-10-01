package com.eduquest.dto;

import com.eduquest.domain.StudentProgress;

import java.time.LocalDateTime;

public class StudentProgressDto {

    private Long id;
    private Long studentId;
    private Long activityId;
    private String activityTitle;
    private Integer score;
    private boolean completed;
    private LocalDateTime completedAt;

    public StudentProgressDto() {}

    public StudentProgressDto(Long id, Long studentId, Long activityId, String activityTitle, Integer score, boolean completed, LocalDateTime completedAt) {
        this.id = id;
        this.studentId = studentId;
        this.activityId = activityId;
        this.activityTitle = activityTitle;
        this.score = score;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public static StudentProgressDto fromEntity(StudentProgress sp) {
        if (sp == null) return null;
        return new StudentProgressDto(
                sp.getId(),
                sp.getStudent() != null ? sp.getStudent().getId() : null,
                sp.getActivity() != null ? sp.getActivity().getId() : null,
                sp.getActivity() != null ? sp.getActivity().getTitle() : null,
                sp.getScore(),
                sp.isCompleted(),
                sp.getCompletedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getActivityTitle() { return activityTitle; }
    public void setActivityTitle(String activityTitle) { this.activityTitle = activityTitle; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
