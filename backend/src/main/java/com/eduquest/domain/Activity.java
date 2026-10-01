package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_id")
    private Long moduleId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "prerequisite_activity_id")
    private Long prerequisiteActivityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "unlock_type")
    private UnlockType unlockType;

    @Column(name = "unlock_value")
    private String unlockValue;

    @Column(name = "xp_reward")
    private Integer xpReward;

    @Column(name = "visible_to_students")
    private Boolean visibleToStudents = true;

    @Column(name = "ai_generated")
    private Boolean aiGenerated = false;

    @Column(name = "ai_generated_by")
    private String aiGeneratedBy;

    @Column(name = "activity_metadata_json", columnDefinition = "TEXT")
    private String activityMetadataJson;

    @Column(name = "created_by_teacher_id")
    private Long createdByTeacherId;

    @Column(name = "assigned_classroom_id")
    private Long assignedClassroomId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Activity() {}

    public Activity(Long id, Long moduleId, String title, String description, Subject subject, ActivityType activityType, ActivityStatus status, Integer displayOrder, Long prerequisiteActivityId, UnlockType unlockType, String unlockValue, Integer xpReward, Boolean visibleToStudents, Boolean aiGenerated, String aiGeneratedBy, String activityMetadataJson, Long createdByTeacherId, Long assignedClassroomId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.moduleId = moduleId;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.activityType = activityType;
        this.status = status;
        this.displayOrder = displayOrder;
        this.prerequisiteActivityId = prerequisiteActivityId;
        this.unlockType = unlockType;
        this.unlockValue = unlockValue;
        this.xpReward = xpReward;
        this.visibleToStudents = visibleToStudents != null ? visibleToStudents : true;
        this.aiGenerated = aiGenerated;
        this.aiGeneratedBy = aiGeneratedBy;
        this.activityMetadataJson = activityMetadataJson;
        this.createdByTeacherId = createdByTeacherId;
        this.assignedClassroomId = assignedClassroomId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ActivityStatus.DRAFT;
        }
        if (this.xpReward == null) {
            this.xpReward = this.activityType == ActivityType.QUIZ ? 20 : 10;
        }
        if (this.visibleToStudents == null) {
            this.visibleToStudents = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public Long getPrerequisiteActivityId() { return prerequisiteActivityId; }
    public void setPrerequisiteActivityId(Long prerequisiteActivityId) { this.prerequisiteActivityId = prerequisiteActivityId; }

    public UnlockType getUnlockType() { return unlockType; }
    public void setUnlockType(UnlockType unlockType) { this.unlockType = unlockType; }

    public String getUnlockValue() { return unlockValue; }
    public void setUnlockValue(String unlockValue) { this.unlockValue = unlockValue; }

    public Integer getXpReward() { return xpReward; }
    public void setXpReward(Integer xpReward) { this.xpReward = xpReward; }

    public Boolean isVisibleToStudents() { return visibleToStudents != null ? visibleToStudents : true; }
    public Boolean getVisibleToStudents() { return visibleToStudents != null ? visibleToStudents : true; }
    public void setVisibleToStudents(Boolean visibleToStudents) { this.visibleToStudents = visibleToStudents; }

    public Boolean getAiGenerated() { return aiGenerated; }
    public void setAiGenerated(Boolean aiGenerated) { this.aiGenerated = aiGenerated; }

    public String getAiGeneratedBy() { return aiGeneratedBy; }
    public void setAiGeneratedBy(String aiGeneratedBy) { this.aiGeneratedBy = aiGeneratedBy; }

    public String getActivityMetadataJson() { return activityMetadataJson; }
    public void setActivityMetadataJson(String activityMetadataJson) { this.activityMetadataJson = activityMetadataJson; }

    public Long getCreatedByTeacherId() { return createdByTeacherId; }
    public void setCreatedByTeacherId(Long createdByTeacherId) { this.createdByTeacherId = createdByTeacherId; }

    public Long getAssignedClassroomId() { return assignedClassroomId; }
    public void setAssignedClassroomId(Long assignedClassroomId) { this.assignedClassroomId = assignedClassroomId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static ActivityBuilder builder() { return new ActivityBuilder(); }

    public static class ActivityBuilder {
        private Long id;
        private Long moduleId;
        private String title;
        private String description;
        private Subject subject;
        private ActivityType activityType;
        private ActivityStatus status;
        private Integer displayOrder;
        private Long prerequisiteActivityId;
        private UnlockType unlockType;
        private String unlockValue;
        private Integer xpReward;
        private Boolean visibleToStudents = true;
        private Boolean aiGenerated = false;
        private String aiGeneratedBy;
        private String activityMetadataJson;
        private Long createdByTeacherId;
        private Long assignedClassroomId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ActivityBuilder id(Long id) { this.id = id; return this; }
        public ActivityBuilder moduleId(Long moduleId) { this.moduleId = moduleId; return this; }
        public ActivityBuilder title(String title) { this.title = title; return this; }
        public ActivityBuilder description(String description) { this.description = description; return this; }
        public ActivityBuilder subject(Subject subject) { this.subject = subject; return this; }
        public ActivityBuilder activityType(ActivityType activityType) { this.activityType = activityType; return this; }
        public ActivityBuilder status(ActivityStatus status) { this.status = status; return this; }
        public ActivityBuilder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public ActivityBuilder prerequisiteActivityId(Long prerequisiteActivityId) { this.prerequisiteActivityId = prerequisiteActivityId; return this; }
        public ActivityBuilder unlockType(UnlockType unlockType) { this.unlockType = unlockType; return this; }
        public ActivityBuilder unlockValue(String unlockValue) { this.unlockValue = unlockValue; return this; }
        public ActivityBuilder xpReward(Integer xpReward) { this.xpReward = xpReward; return this; }
        public ActivityBuilder visibleToStudents(Boolean visibleToStudents) { this.visibleToStudents = visibleToStudents; return this; }
        public ActivityBuilder aiGenerated(Boolean aiGenerated) { this.aiGenerated = aiGenerated; return this; }
        public ActivityBuilder aiGeneratedBy(String aiGeneratedBy) { this.aiGeneratedBy = aiGeneratedBy; return this; }
        public ActivityBuilder activityMetadataJson(String activityMetadataJson) { this.activityMetadataJson = activityMetadataJson; return this; }
        public ActivityBuilder createdByTeacherId(Long createdByTeacherId) { this.createdByTeacherId = createdByTeacherId; return this; }
        public ActivityBuilder assignedClassroomId(Long assignedClassroomId) { this.assignedClassroomId = assignedClassroomId; return this; }
        public ActivityBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ActivityBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Activity build() {
            return new Activity(id, moduleId, title, description, subject, activityType, status, displayOrder, prerequisiteActivityId, unlockType, unlockValue, xpReward, visibleToStudents, aiGenerated, aiGeneratedBy, activityMetadataJson, createdByTeacherId, assignedClassroomId, createdAt, updatedAt);
        }
    }
}
