package com.eduquest.ai.controller;

import com.eduquest.ai.dto.ChatRequest;
import com.eduquest.ai.dto.ChatResponse;
import com.eduquest.ai.dto.ChatSessionDto;
import com.eduquest.ai.dto.ChatHistoryPage;
import com.eduquest.ai.service.AiTutorService;
import com.eduquest.ai.service.LanguageService;
import com.eduquest.domain.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiTutorController {

    private final AiTutorService aiTutorService;
    private final LanguageService languageService;

    @Autowired
    public AiTutorController(AiTutorService aiTutorService, LanguageService languageService) {
        this.aiTutorService = aiTutorService;
        this.languageService = languageService;
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserAccount) {
            UserAccount user = (UserAccount) authentication.getPrincipal();
            return user.getId();
        }
        throw new AccessDeniedException("Authenticated EduQuest account required");
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        Long userId = extractUserId(authentication);
        if (request.getAction() == null || request.getAction().isBlank()) {
            request.setAction("CHAT");
        }
        ChatResponse response = aiTutorService.processMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/explain")
    public ResponseEntity<ChatResponse> explain(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        Long userId = extractUserId(authentication);
        request.setAction("EXPLAIN");
        ChatResponse response = aiTutorService.processMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simplify")
    public ResponseEntity<ChatResponse> simplify(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        Long userId = extractUserId(authentication);
        request.setAction("SIMPLIFY");
        ChatResponse response = aiTutorService.processMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/hint")
    public ResponseEntity<ChatResponse> hint(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        Long userId = extractUserId(authentication);
        request.setAction("HINT");
        ChatResponse response = aiTutorService.processMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/practice")
    public ResponseEntity<ChatResponse> practice(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        Long userId = extractUserId(authentication);
        request.setAction("PRACTICE");
        ChatResponse response = aiTutorService.processMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionDto>> getSessions(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(aiTutorService.getUserSessions(userId));
    }

    @GetMapping("/session-summaries")
    public ResponseEntity<List<ChatSessionDto>> getSessionSummaries(Authentication authentication) {
        return ResponseEntity.ok(aiTutorService.getUserSessionSummaries(extractUserId(authentication)));
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ChatSessionDto> getSessionHistory(@PathVariable Long sessionId, Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(aiTutorService.getSessionById(sessionId, userId));
    }

    @GetMapping("/history/{sessionId}/messages")
    public ResponseEntity<ChatHistoryPage> getMessages(@PathVariable Long sessionId,
            @RequestParam(required = false) Long beforeId, Authentication authentication) {
        return ResponseEntity.ok(aiTutorService.getMessages(sessionId, extractUserId(authentication), beforeId));
    }

    @GetMapping("/languages")
    public ResponseEntity<?> getLanguages() { return ResponseEntity.ok(languageService.supportedLanguages()); }

    @PostMapping("/session")
    public ResponseEntity<ChatSessionDto> createSession(@RequestParam(required = false) String title,
                                                        @RequestParam(required = false) String mode,
                                                        @RequestParam(required = false) Long lessonId,
                                                        Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(aiTutorService.createSession(userId, title, mode, lessonId));
    }
}
