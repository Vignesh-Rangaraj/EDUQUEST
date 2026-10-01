package com.eduquest.ai.dto;

import com.eduquest.ai.entity.ChatSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ChatSessionDto {
    private Long id;
    private Long userId;
    private String title;
    private String mode;
    private Long lessonId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChatMessageDto> messages;

    public ChatSessionDto() {}

    public ChatSessionDto(Long id, Long userId, String title, String mode, Long lessonId, LocalDateTime createdAt, LocalDateTime updatedAt, List<ChatMessageDto> messages) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.mode = mode;
        this.lessonId = lessonId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messages = messages;
    }

    public static ChatSessionDto fromEntity(ChatSession entity) {
        List<ChatMessageDto> msgDtos = entity.getMessages() != null
                ? entity.getMessages().stream().map(ChatMessageDto::fromEntity).collect(Collectors.toList())
                : List.of();

        return new ChatSessionDto(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getMode(),
                entity.getLessonId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                msgDtos
        );
    }

    public static ChatSessionDto summaryFromEntity(ChatSession entity) {
        return new ChatSessionDto(entity.getId(), entity.getUserId(), entity.getTitle(), entity.getMode(),
                entity.getLessonId(), entity.getCreatedAt(), entity.getUpdatedAt(), List.of());
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

    public List<ChatMessageDto> getMessages() { return messages; }
    public void setMessages(List<ChatMessageDto> messages) { this.messages = messages; }
}
