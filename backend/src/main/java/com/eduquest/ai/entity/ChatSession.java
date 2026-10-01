package com.eduquest.ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_sessions")
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title")
    private String title;

    @Column(name = "mode")
    private String mode;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("session")
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatSession() {}

    public ChatSession(Long id, Long userId, String title, String mode, Long lessonId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.mode = mode;
        this.lessonId = lessonId;
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

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    public static ChatSessionBuilder builder() { return new ChatSessionBuilder(); }

    public static class ChatSessionBuilder {
        private Long id;
        private Long userId;
        private String title;
        private String mode;
        private Long lessonId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ChatSessionBuilder id(Long id) { this.id = id; return this; }
        public ChatSessionBuilder userId(Long userId) { this.userId = userId; return this; }
        public ChatSessionBuilder title(String title) { this.title = title; return this; }
        public ChatSessionBuilder mode(String mode) { this.mode = mode; return this; }
        public ChatSessionBuilder lessonId(Long lessonId) { this.lessonId = lessonId; return this; }
        public ChatSessionBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ChatSessionBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ChatSession build() {
            return new ChatSession(id, userId, title, mode, lessonId, createdAt, updatedAt);
        }
    }
}
