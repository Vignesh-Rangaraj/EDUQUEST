package com.eduquest.ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private ChatSession session;

    @Column(name = "sender", nullable = false, length = 20)
    private String sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "language", length = 12)
    private String language = "en";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChatMessage() {}

    public ChatMessage(Long id, ChatSession session, String sender, String content, LocalDateTime createdAt) {
        this.id = id;
        this.session = session;
        this.sender = sender;
        this.content = content;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChatSession getSession() { return session; }
    public void setSession(ChatSession session) { this.session = session; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLanguage() { return language == null || language.isBlank() ? "en" : language; }
    public void setLanguage(String language) { this.language = language; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ChatMessageBuilder builder() { return new ChatMessageBuilder(); }

    public static class ChatMessageBuilder {
        private Long id;
        private ChatSession session;
        private String sender;
        private String content;
        private String language = "en";
        private LocalDateTime createdAt;

        public ChatMessageBuilder id(Long id) { this.id = id; return this; }
        public ChatMessageBuilder session(ChatSession session) { this.session = session; return this; }
        public ChatMessageBuilder sender(String sender) { this.sender = sender; return this; }
        public ChatMessageBuilder content(String content) { this.content = content; return this; }
        public ChatMessageBuilder language(String language) { this.language = language; return this; }
        public ChatMessageBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ChatMessage build() {
            ChatMessage message = new ChatMessage(id, session, sender, content, createdAt);
            message.setLanguage(language);
            return message;
        }
    }
}
