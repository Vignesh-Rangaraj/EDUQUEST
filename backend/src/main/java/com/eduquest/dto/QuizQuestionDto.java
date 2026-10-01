package com.eduquest.dto;

import com.eduquest.domain.QuizQuestion;

public class QuizQuestionDto {

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

    public QuizQuestionDto() {}

    public QuizQuestionDto(Long id, Long activityId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String explanation, Integer displayOrder) {
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
    }

    public static QuizQuestionDto fromEntity(QuizQuestion qq) {
        if (qq == null) return null;
        return new QuizQuestionDto(
                qq.getId(),
                qq.getActivityId(),
                qq.getQuestionText(),
                qq.getOptionA(),
                qq.getOptionB(),
                qq.getOptionC(),
                qq.getOptionD(),
                qq.getCorrectAnswer(),
                qq.getExplanation(),
                qq.getDisplayOrder()
        );
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
}
