package com.eduquest.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_configurations")
public class GameConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false, unique = true)
    private Long activityId;

    @Column(name = "json_configuration", columnDefinition = "TEXT", nullable = false)
    private String jsonConfiguration;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public GameConfiguration() {}

    public GameConfiguration(Long id, Long activityId, String jsonConfiguration, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.activityId = activityId;
        this.jsonConfiguration = jsonConfiguration;
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

    public String getJsonConfiguration() { return jsonConfiguration; }
    public void setJsonConfiguration(String jsonConfiguration) { this.jsonConfiguration = jsonConfiguration; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static GameConfigurationBuilder builder() { return new GameConfigurationBuilder(); }

    public static class GameConfigurationBuilder {
        private Long id;
        private Long activityId;
        private String jsonConfiguration;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public GameConfigurationBuilder id(Long id) { this.id = id; return this; }
        public GameConfigurationBuilder activityId(Long activityId) { this.activityId = activityId; return this; }
        public GameConfigurationBuilder jsonConfiguration(String jsonConfiguration) { this.jsonConfiguration = jsonConfiguration; return this; }
        public GameConfigurationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public GameConfigurationBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public GameConfiguration build() {
            return new GameConfiguration(id, activityId, jsonConfiguration, createdAt, updatedAt);
        }
    }
}
