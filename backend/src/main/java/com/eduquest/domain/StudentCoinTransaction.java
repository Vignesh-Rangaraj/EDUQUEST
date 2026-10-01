package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_coin_transactions")
public class StudentCoinTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "coins_awarded", nullable = false)
    private Integer coinsAwarded;

    @Column(nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public StudentCoinTransaction() {}

    public StudentCoinTransaction(Long id, Long studentId, Integer coinsAwarded, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.coinsAwarded = coinsAwarded;
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

    public Integer getCoinsAwarded() { return coinsAwarded; }
    public void setCoinsAwarded(Integer coinsAwarded) { this.coinsAwarded = coinsAwarded; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static StudentCoinTransactionBuilder builder() { return new StudentCoinTransactionBuilder(); }

    public static class StudentCoinTransactionBuilder {
        private Long id;
        private Long studentId;
        private Integer coinsAwarded;
        private String reason;
        private LocalDateTime createdAt;

        public StudentCoinTransactionBuilder id(Long id) { this.id = id; return this; }
        public StudentCoinTransactionBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentCoinTransactionBuilder coinsAwarded(Integer coinsAwarded) { this.coinsAwarded = coinsAwarded; return this; }
        public StudentCoinTransactionBuilder reason(String reason) { this.reason = reason; return this; }
        public StudentCoinTransactionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public StudentCoinTransaction build() {
            return new StudentCoinTransaction(id, studentId, coinsAwarded, reason, createdAt);
        }
    }
}
