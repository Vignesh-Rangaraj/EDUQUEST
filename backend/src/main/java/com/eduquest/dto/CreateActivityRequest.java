package com.eduquest.dto;

import com.eduquest.domain.ActivityType;
import com.eduquest.domain.Subject;
import com.eduquest.domain.UnlockType;

public class CreateActivityRequest {

    private String title;
    private String description;
    private Subject subject;
    private ActivityType activityType;
    private Long assignedClassroomId;
    private Long moduleId;
    private Integer displayOrder;
    private Integer xpReward;
    private Long prerequisiteActivityId;
    private UnlockType unlockType;
    private String unlockValue;
    private String activityMetadataJson;

    public CreateActivityRequest() {}

    public CreateActivityRequest(String title, String description, Subject subject, ActivityType activityType, Long assignedClassroomId) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.activityType = activityType;
        this.assignedClassroomId = assignedClassroomId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }

    public Long getAssignedClassroomId() { return assignedClassroomId; }
    public void setAssignedClassroomId(Long assignedClassroomId) { this.assignedClassroomId = assignedClassroomId; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Integer getXpReward() { return xpReward; }
    public void setXpReward(Integer xpReward) { this.xpReward = xpReward; }

    public Long getPrerequisiteActivityId() { return prerequisiteActivityId; }
    public void setPrerequisiteActivityId(Long prerequisiteActivityId) { this.prerequisiteActivityId = prerequisiteActivityId; }

    public UnlockType getUnlockType() { return unlockType; }
    public void setUnlockType(UnlockType unlockType) { this.unlockType = unlockType; }

    public String getUnlockValue() { return unlockValue; }
    public void setUnlockValue(String unlockValue) { this.unlockValue = unlockValue; }

    public String getActivityMetadataJson() { return activityMetadataJson; }
    public void setActivityMetadataJson(String activityMetadataJson) { this.activityMetadataJson = activityMetadataJson; }
}
