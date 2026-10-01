package com.eduquest.ai.dto;

import com.eduquest.ai.entity.ChatMessage;
import java.time.LocalDateTime;

public class ChatMessageDto {
    private Long id;
    private Long sessionId;
    private String sender;
    private String content;
    private String language;
    private LocalDateTime createdAt;

    public ChatMessageDto() {}

    public ChatMessageDto(Long id, Long sessionId, String sender, String content, LocalDateTime createdAt) {
        this(id, sessionId, sender, content, "en", createdAt);
    }

    public ChatMessageDto(Long id, Long sessionId, String sender, String content, String language, LocalDateTime createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.sender = sender;
        this.content = content;
        this.language = language;
        this.createdAt = createdAt;
    }

    public static ChatMessageDto fromEntity(ChatMessage entity) {
        return new ChatMessageDto(
            entity.getId(),
            entity.getSession() != null ? entity.getSession().getId() : null,
            entity.getSender(),
            entity.getContent(),
            entity.getLanguage(),
            entity.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
