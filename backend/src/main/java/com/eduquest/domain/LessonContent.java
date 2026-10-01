package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_contents")
public class LessonContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false, unique = true)
    private Long activityId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public LessonContent() {}

    public LessonContent(Long id, Long activityId, String content, Integer estimatedMinutes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.activityId = activityId;
        this.content = content;
        this.estimatedMinutes = estimatedMinutes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.estimatedMinutes == null) this.estimatedMinutes = 15;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static LessonContentBuilder builder() { return new LessonContentBuilder(); }

    public static class LessonContentBuilder {
        private Long id;
        private Long activityId;
        private String content;
        private Integer estimatedMinutes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public LessonContentBuilder id(Long id) { this.id = id; return this; }
        public LessonContentBuilder activityId(Long activityId) { this.activityId = activityId; return this; }
        public LessonContentBuilder content(String content) { this.content = content; return this; }
        public LessonContentBuilder estimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; return this; }
        public LessonContentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public LessonContentBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public LessonContent build() {
            return new LessonContent(id, activityId, content, estimatedMinutes, createdAt, updatedAt);
        }
    }
}
