package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_activity_events")
public class StudentActivityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private StudentActivityEventType eventType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public StudentActivityEvent() {}

    public StudentActivityEvent(Long id, Long studentId, Long activityId, StudentActivityEventType eventType, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.activityId = activityId;
        this.eventType = eventType;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public StudentActivityEventType getEventType() { return eventType; }
    public void setEventType(StudentActivityEventType eventType) { this.eventType = eventType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static StudentActivityEventBuilder builder() { return new StudentActivityEventBuilder(); }

    public static class StudentActivityEventBuilder {
        private Long id;
        private Long studentId;
        private Long activityId;
        private StudentActivityEventType eventType;
        private LocalDateTime createdAt;

        public StudentActivityEventBuilder id(Long id) { this.id = id; return this; }
        public StudentActivityEventBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentActivityEventBuilder activityId(Long activityId) { this.activityId = activityId; return this; }
        public StudentActivityEventBuilder eventType(StudentActivityEventType eventType) { this.eventType = eventType; return this; }
        public StudentActivityEventBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public StudentActivityEvent build() {
            return new StudentActivityEvent(id, studentId, activityId, eventType, createdAt);
        }
    }
}
