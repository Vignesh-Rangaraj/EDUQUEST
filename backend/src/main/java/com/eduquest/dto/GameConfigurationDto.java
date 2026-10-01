package com.eduquest.dto;

import com.eduquest.domain.GameConfiguration;

public class GameConfigurationDto {

    private Long id;
    private Long activityId;
    private String jsonConfiguration;

    public GameConfigurationDto() {}

    public GameConfigurationDto(Long id, Long activityId, String jsonConfiguration) {
        this.id = id;
        this.activityId = activityId;
        this.jsonConfiguration = jsonConfiguration;
    }

    public static GameConfigurationDto fromEntity(GameConfiguration gc) {
        if (gc == null) return null;
        return new GameConfigurationDto(gc.getId(), gc.getActivityId(), gc.getJsonConfiguration());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getJsonConfiguration() { return jsonConfiguration; }
    public void setJsonConfiguration(String jsonConfiguration) { this.jsonConfiguration = jsonConfiguration; }
}
