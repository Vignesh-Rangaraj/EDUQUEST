package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_xp_transactions")
public class StudentXpTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "xp_awarded", nullable = false)
    private Integer xpAwarded;

    @Column(nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public StudentXpTransaction() {}

    public StudentXpTransaction(Long id, Long studentId, Long activityId, Integer xpAwarded, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.activityId = activityId;
        this.xpAwarded = xpAwarded;
        this.reason = reason;
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

    public Integer getXpAwarded() { return xpAwarded; }
    public void setXpAwarded(Integer xpAwarded) { this.xpAwarded = xpAwarded; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static StudentXpTransactionBuilder builder() { return new StudentXpTransactionBuilder(); }

    public static class StudentXpTransactionBuilder {
        private Long id;
        private Long studentId;
        private Long activityId;
        private Integer xpAwarded;
        private String reason;
        private LocalDateTime createdAt;

        public StudentXpTransactionBuilder id(Long id) { this.id = id; return this; }
        public StudentXpTransactionBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentXpTransactionBuilder activityId(Long activityId) { this.activityId = activityId; return this; }
        public StudentXpTransactionBuilder xpAwarded(Integer xpAwarded) { this.xpAwarded = xpAwarded; return this; }
        public StudentXpTransactionBuilder reason(String reason) { this.reason = reason; return this; }
        public StudentXpTransactionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public StudentXpTransaction build() {
            return new StudentXpTransaction(id, studentId, activityId, xpAwarded, reason, createdAt);
        }
    }
}
