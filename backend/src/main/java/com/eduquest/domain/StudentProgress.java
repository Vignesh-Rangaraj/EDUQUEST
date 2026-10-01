package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_progress",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_activity", columnNames = {"student_id", "activity_id"})
    }
)
public class StudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StudentProgress() {}

    public StudentProgress(Long id, Student student, Activity activity, Integer score, boolean completed, LocalDateTime completedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.student = student;
        this.activity = activity;
        this.score = score;
        this.completed = completed;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.completedAt == null && this.completed) {
            this.completedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static StudentProgressBuilder builder() { return new StudentProgressBuilder(); }

    public static class StudentProgressBuilder {
        private Long id;
        private Student student;
        private Activity activity;
        private Integer score;
        private boolean completed;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public StudentProgressBuilder id(Long id) { this.id = id; return this; }
        public StudentProgressBuilder student(Student student) { this.student = student; return this; }
        public StudentProgressBuilder activity(Activity activity) { this.activity = activity; return this; }
        public StudentProgressBuilder score(Integer score) { this.score = score; return this; }
        public StudentProgressBuilder completed(boolean completed) { this.completed = completed; return this; }
        public StudentProgressBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public StudentProgressBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentProgressBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public StudentProgress build() {
            return new StudentProgress(id, student, activity, score, completed, completedAt, createdAt, updatedAt);
        }
    }
}
