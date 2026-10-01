package com.eduquest.ai.dto;

import java.time.LocalDateTime;

public class ChatResponse {
    private Long sessionId;
    private String reply;
    private Long messageId;
    private LocalDateTime timestamp;

    public ChatResponse() {}

    public ChatResponse(Long sessionId, String reply, Long messageId, LocalDateTime timestamp) {
        this.sessionId = sessionId;
        this.reply = reply;
        this.messageId = messageId;
        this.timestamp = timestamp;
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
