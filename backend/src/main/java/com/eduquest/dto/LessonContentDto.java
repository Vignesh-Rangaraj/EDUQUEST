package com.eduquest.dto;

import com.eduquest.domain.LessonContent;

public class LessonContentDto {

    private Long id;
    private Long activityId;
    private String content;
    private Integer estimatedMinutes;

    public LessonContentDto() {}

    public LessonContentDto(Long id, Long activityId, String content, Integer estimatedMinutes) {
        this.id = id;
        this.activityId = activityId;
        this.content = content;
        this.estimatedMinutes = estimatedMinutes;
    }

    public static LessonContentDto fromEntity(LessonContent lc) {
        if (lc == null) return null;
        return new LessonContentDto(lc.getId(), lc.getActivityId(), lc.getContent(), lc.getEstimatedMinutes());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
}
