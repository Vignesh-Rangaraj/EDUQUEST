package com.eduquest.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChatRequest {
    private Long sessionId;
    @NotBlank
    @Size(max = 4000)
    private String message;
    private Long lessonId;
    private String action; // CHAT, EXPLAIN, SIMPLIFY, HINT, PRACTICE
    @Pattern(regexp = "(?i)en|ta|hi|ml|te|kn|english|tamil|hindi|malayalam|telugu|kannada")
    private String language;

    public ChatRequest() {}

    public ChatRequest(Long sessionId, String message, Long lessonId, String action) {
        this.sessionId = sessionId;
        this.message = message;
        this.lessonId = lessonId;
        this.action = action;
    }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
