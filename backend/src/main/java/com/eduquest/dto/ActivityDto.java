package com.eduquest.dto;

import com.eduquest.domain.Activity;
import com.eduquest.domain.ActivityStatus;
import com.eduquest.domain.ActivityType;
import com.eduquest.domain.Subject;

import java.time.LocalDateTime;

public class ActivityDto {

    private Long id;
    private Long moduleId;
    private String title;
    private String description;
    private Subject subject;
    private ActivityType activityType;
    private ActivityStatus status;
    private Integer displayOrder;
    private Integer xpReward;
    private String statusBadge; // COMPLETED, CURRENT, LOCKED, NOT_STARTED
    private boolean completed;
    private Long createdByTeacherId;
    private Long assignedClassroomId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ActivityDto() {}

    public ActivityDto(Long id, Long moduleId, String title, String description, Subject subject, ActivityType activityType, ActivityStatus status, Integer displayOrder, Integer xpReward, String statusBadge, boolean completed, Long createdByTeacherId, Long assignedClassroomId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.moduleId = moduleId;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.activityType = activityType;
        this.status = status;
        this.displayOrder = displayOrder;
        this.xpReward = xpReward;
        this.statusBadge = statusBadge;
        this.completed = completed;
        this.createdByTeacherId = createdByTeacherId;
        this.assignedClassroomId = assignedClassroomId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ActivityDto fromEntity(Activity activity) {
        if (activity == null) return null;
        return new ActivityDto(
                activity.getId(),
                activity.getModuleId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getSubject(),
                activity.getActivityType(),
                activity.getStatus(),
                activity.getDisplayOrder(),
                activity.getXpReward() != null ? activity.getXpReward() : 10,
                "NOT_STARTED",
                false,
                activity.getCreatedByTeacherId(),
                activity.getAssignedClassroomId(),
                activity.getCreatedAt(),
                activity.getUpdatedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }

    public ActivityStatus getStatus() { return status; }
    public void setStatus(ActivityStatus status) { this.status = status; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Integer getXpReward() { return xpReward; }
    public void setXpReward(Integer xpReward) { this.xpReward = xpReward; }

    public String getStatusBadge() { return statusBadge; }
    public void setStatusBadge(String statusBadge) { this.statusBadge = statusBadge; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Long getCreatedByTeacherId() { return createdByTeacherId; }
    public void setCreatedByTeacherId(Long createdByTeacherId) { this.createdByTeacherId = createdByTeacherId; }

    public Long getAssignedClassroomId() { return assignedClassroomId; }
    public void setAssignedClassroomId(Long assignedClassroomId) { this.assignedClassroomId = assignedClassroomId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
