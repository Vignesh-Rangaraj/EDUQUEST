package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Column(name = "option_c", nullable = false)
    private String optionC;

    @Column(name = "option_d", nullable = false)
    private String optionD;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public QuizQuestion() {}

    public QuizQuestion(Long id, Long activityId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String explanation, Integer displayOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.activityId = activityId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static QuizQuestionBuilder builder() { return new QuizQuestionBuilder(); }

    public static class QuizQuestionBuilder {
        private Long id;
        private Long activityId;
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctAnswer;
        private String explanation;
        private Integer displayOrder;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public QuizQuestionBuilder id(Long id) { this.id = id; return this; }
        public QuizQuestionBuilder activityId(Long activityId) { this.activityId = activityId; return this; }
        public QuizQuestionBuilder questionText(String questionText) { this.questionText = questionText; return this; }
        public QuizQuestionBuilder optionA(String optionA) { this.optionA = optionA; return this; }
        public QuizQuestionBuilder optionB(String optionB) { this.optionB = optionB; return this; }
        public QuizQuestionBuilder optionC(String optionC) { this.optionC = optionC; return this; }
        public QuizQuestionBuilder optionD(String optionD) { this.optionD = optionD; return this; }
        public QuizQuestionBuilder correctAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; return this; }
        public QuizQuestionBuilder explanation(String explanation) { this.explanation = explanation; return this; }
        public QuizQuestionBuilder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
        public QuizQuestionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public QuizQuestionBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public QuizQuestion build() {
            return new QuizQuestion(id, activityId, questionText, optionA, optionB, optionC, optionD, correctAnswer, explanation, displayOrder, createdAt, updatedAt);
        }
    }
}
