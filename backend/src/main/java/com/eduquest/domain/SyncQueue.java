package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_queue")
public class SyncQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_queue_item_id")
    private String clientQueueItemId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "payload_json", columnDefinition = "TEXT", nullable = false)
    private String payloadJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public SyncQueue() {}

    public SyncQueue(Long id, String clientQueueItemId, Long studentId, String actionType, String payloadJson, SyncStatus status, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.clientQueueItemId = clientQueueItemId;
        this.studentId = studentId;
        this.actionType = actionType;
        this.payloadJson = payloadJson;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        if (this.status == null) {
            this.status = SyncStatus.PENDING;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClientQueueItemId() { return clientQueueItemId; }
    public void setClientQueueItemId(String clientQueueItemId) { this.clientQueueItemId = clientQueueItemId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public SyncStatus getStatus() { return status; }
    public void setStatus(SyncStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public static SyncQueueBuilder builder() { return new SyncQueueBuilder(); }

    public static class SyncQueueBuilder {
        private Long id;
        private String clientQueueItemId;
        private Long studentId;
        private String actionType;
        private String payloadJson;
        private SyncStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;

        public SyncQueueBuilder id(Long id) { this.id = id; return this; }
        public SyncQueueBuilder clientQueueItemId(String clientQueueItemId) { this.clientQueueItemId = clientQueueItemId; return this; }
        public SyncQueueBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public SyncQueueBuilder actionType(String actionType) { this.actionType = actionType; return this; }
        public SyncQueueBuilder payloadJson(String payloadJson) { this.payloadJson = payloadJson; return this; }
        public SyncQueueBuilder status(SyncStatus status) { this.status = status; return this; }
        public SyncQueueBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public SyncQueueBuilder processedAt(LocalDateTime processedAt) { this.processedAt = processedAt; return this; }

        public SyncQueue build() {
            return new SyncQueue(id, clientQueueItemId, studentId, actionType, payloadJson, status, createdAt, processedAt);
        }
    }
}
