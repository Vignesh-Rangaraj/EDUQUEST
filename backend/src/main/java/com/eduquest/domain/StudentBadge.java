package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_badges",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_badge", columnNames = {"student_id", "badge_code"})
    }
)
public class StudentBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "badge_code", nullable = false)
    private String badgeCode;

    @Column(name = "badge_name", nullable = false)
    private String badgeName;

    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;

    public StudentBadge() {}

    public StudentBadge(Long id, Long studentId, String badgeCode, String badgeName, LocalDateTime earnedAt) {
        this.id = id;
        this.studentId = studentId;
        this.badgeCode = badgeCode;
        this.badgeName = badgeName;
        this.earnedAt = earnedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.earnedAt == null) {
            this.earnedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getBadgeCode() { return badgeCode; }
    public void setBadgeCode(String badgeCode) { this.badgeCode = badgeCode; }

    public String getBadgeName() { return badgeName; }
    public void setBadgeName(String badgeName) { this.badgeName = badgeName; }

    public LocalDateTime getEarnedAt() { return earnedAt; }
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; }

    public static StudentBadgeBuilder builder() { return new StudentBadgeBuilder(); }

    public static class StudentBadgeBuilder {
        private Long id;
        private Long studentId;
        private String badgeCode;
        private String badgeName;
        private LocalDateTime earnedAt;

        public StudentBadgeBuilder id(Long id) { this.id = id; return this; }
        public StudentBadgeBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentBadgeBuilder badgeCode(String badgeCode) { this.badgeCode = badgeCode; return this; }
        public StudentBadgeBuilder badgeName(String badgeName) { this.badgeName = badgeName; return this; }
        public StudentBadgeBuilder earnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; return this; }

        public StudentBadge build() {
            return new StudentBadge(id, studentId, badgeCode, badgeName, earnedAt);
        }
    }
}
