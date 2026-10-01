package com.eduquest.dto;

public class SyncItemDto {

    private String id; // local IndexedDB queue id
    private String actionType; // COMPLETE_ACTIVITY, UPDATE_PROGRESS, etc.
    private Long studentId;
    private Long activityId;
    private Integer score;
    private String completedAt;
    private String payload;

    public SyncItemDto() {}

    public SyncItemDto(String id, String actionType, Long studentId, Long activityId, Integer score, String completedAt, String payload) {
        this.id = id;
        this.actionType = actionType;
        this.studentId = studentId;
        this.activityId = activityId;
        this.score = score;
        this.completedAt = completedAt;
        this.payload = payload;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
