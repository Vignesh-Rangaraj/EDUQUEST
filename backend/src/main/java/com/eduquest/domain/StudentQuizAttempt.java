package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_quiz_attempts")
public class StudentQuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Column(nullable = false)
    private Integer score;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers;

    @Column(name = "answers_json", columnDefinition = "TEXT")
    private String answersJson;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    public StudentQuizAttempt() {}

    public StudentQuizAttempt(Long id, Long studentId, Long activityId, Integer score, Integer totalQuestions, Integer correctAnswers, String answersJson, LocalDateTime completedAt) {
        this.id = id;
        this.studentId = studentId;
        this.activityId = activityId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.answersJson = answersJson;
        this.completedAt = completedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public String getAnswersJson() { return answersJson; }
    public void setAnswersJson(String answersJson) { this.answersJson = answersJson; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public static StudentQuizAttemptBuilder builder() { return new StudentQuizAttemptBuilder(); }

    public static class StudentQuizAttemptBuilder {
        private Long id;
        private Long studentId;
        private Long activityId;
        private Integer score;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private String answersJson;
        private LocalDateTime completedAt;

        public StudentQuizAttemptBuilder id(Long id) { this.id = id; return this; }
        public StudentQuizAttemptBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public StudentQuizAttemptBuilder activityId(Long activityId) { this.activityId = activityId; return this; }
        public StudentQuizAttemptBuilder score(Integer score) { this.score = score; return this; }
        public StudentQuizAttemptBuilder totalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; return this; }
        public StudentQuizAttemptBuilder correctAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; return this; }
        public StudentQuizAttemptBuilder answersJson(String answersJson) { this.answersJson = answersJson; return this; }
        public StudentQuizAttemptBuilder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }

        public StudentQuizAttempt build() {
            return new StudentQuizAttempt(id, studentId, activityId, score, totalQuestions, correctAnswers, answersJson, completedAt);
        }
    }
}
