package com.eduquest.dto;

import java.util.HashMap;
import java.util.Map;

public class SubmitQuizRequest {

    private Long activityId;
    private Map<Long, String> userAnswers = new HashMap<>(); // questionId -> selectedOption ("A", "B", "C", "D")

    public SubmitQuizRequest() {}

    public SubmitQuizRequest(Long activityId, Map<Long, String> userAnswers) {
        this.activityId = activityId;
        this.userAnswers = userAnswers;
    }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Map<Long, String> getUserAnswers() { return userAnswers; }
    public void setUserAnswers(Map<Long, String> userAnswers) { this.userAnswers = userAnswers; }
}
