package com.eduquest.ai.service;

import com.eduquest.ai.dto.ChatMessageDto;
import com.eduquest.ai.dto.ChatRequest;
import com.eduquest.ai.dto.ChatResponse;
import com.eduquest.ai.dto.ChatSessionDto;
import com.eduquest.ai.entity.ChatMessage;
import com.eduquest.ai.entity.ChatSession;
import com.eduquest.ai.repository.ChatMessageRepository;
import com.eduquest.ai.repository.ChatSessionRepository;
import com.eduquest.domain.Activity;
import com.eduquest.repository.ActivityRepository;
import com.eduquest.repository.LessonContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AiTutorService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final GeminiService geminiService;
    private final ActivityRepository activityRepository;
    private final LessonContentRepository lessonContentRepository;
    private final LanguageService languageService;

    @Autowired
    public AiTutorService(ChatSessionRepository sessionRepository,
                          ChatMessageRepository messageRepository,
                          GeminiService geminiService,
                          ActivityRepository activityRepository,
                          LessonContentRepository lessonContentRepository,
                          LanguageService languageService) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.geminiService = geminiService;
        this.activityRepository = activityRepository;
        this.lessonContentRepository = lessonContentRepository;
        this.languageService = languageService;
    }

    @Transactional
    public ChatResponse processMessage(Long userId, ChatRequest request) {
        String message = request.getMessage() == null ? "" : request.getMessage().replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "").trim();
        if (message.isBlank() || message.length() > 4000) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Message must contain 1 to 4000 characters");
        String language = languageService.normalize(request.getLanguage());
        String action = request.getAction() == null ? "CHAT" : request.getAction().trim().toUpperCase();
        if (!List.of("CHAT", "EXPLAIN", "SIMPLIFY", "HINT", "PRACTICE").contains(action))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Unsupported tutor mode");
        request.setMessage(message);
        request.setLanguage(language);
        request.setAction(action);
        ChatSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findByIdAndUserId(request.getSessionId(), userId)
                    .orElseGet(() -> createNewSession(userId, request.getMessage(), request.getAction(), request.getLessonId()));
        } else {
            session = createNewSession(userId, request.getMessage(), request.getAction(), request.getLessonId());
        }

        // Save User Message
        ChatMessage userMsg = ChatMessage.builder()
                .session(session)
                .sender("USER")
                .content(request.getMessage())
                .language(language)
                .createdAt(LocalDateTime.now())
                .build();
        messageRepository.save(userMsg);

        // Fetch lesson context if present
        String lessonContext = "";
        if (request.getLessonId() != null) {
            Optional<Activity> activityOpt = activityRepository.findById(request.getLessonId());
            if (activityOpt.isPresent()) {
                Activity act = activityOpt.get();
                String content = lessonContentRepository.findByActivityId(act.getId()).map(l -> l.getContent()).orElse("");
                if (content == null) content = "";
                lessonContext = "Lesson Title: " + act.getTitle()
                        + (act.getDescription() != null ? "\nLesson summary: " + act.getDescription() : "")
                        + (content.isBlank() ? "" : "\nLesson content:\n" + content.substring(0, Math.min(content.length(), 12000)));
            }
        }

        // Call Gemini Service
        String aiReply = geminiService.generateContent(message, action, lessonContext, languageService.displayName(language));

        // Save AI Message
        ChatMessage aiMsg = ChatMessage.builder()
                .session(session)
                .sender("AI")
                .content(aiReply)
                .language(language)
                .createdAt(LocalDateTime.now())
                .build();
        ChatMessage savedAiMsg = messageRepository.save(aiMsg);

        // Update session timestamp
        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        return new ChatResponse(
                session.getId(),
                savedAiMsg.getContent(),
                savedAiMsg.getId(),
                savedAiMsg.getCreatedAt()
        );
    }

    @Transactional
    public ChatSessionDto createSession(Long userId, String title, String mode, Long lessonId) {
        ChatSession session = createNewSession(userId, title, mode, lessonId);
        return ChatSessionDto.fromEntity(session);
    }

    private ChatSession createNewSession(Long userId, String firstMessage, String mode, Long lessonId) {
        String title = (firstMessage != null && !firstMessage.isBlank())
                ? (firstMessage.length() > 30 ? firstMessage.substring(0, 30) + "..." : firstMessage)
                : "AI Learning Session";

        ChatSession session = ChatSession.builder()
                .userId(userId)
                .title(title)
                .mode(mode != null ? mode : "CHAT")
                .lessonId(lessonId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDto> getUserSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(ChatSessionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDto> getUserSessionSummaries(Long userId) {
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(ChatSessionDto::summaryFromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatSessionDto getSessionById(Long sessionId, Long userId) {
        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        return ChatSessionDto.fromEntity(session);
    }

    @Transactional(readOnly = true)
    public com.eduquest.ai.dto.ChatHistoryPage getMessages(Long sessionId, Long userId, Long beforeId) {
        sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        List<ChatMessage> rows = beforeId == null
                ? messageRepository.findTop51BySessionIdOrderByIdDesc(sessionId)
                : messageRepository.findTop51BySessionIdAndIdLessThanOrderByIdDesc(sessionId, beforeId);
        boolean more = rows.size() > 50;
        if (more) rows = rows.subList(0, 50);
        Long next = more && !rows.isEmpty() ? rows.get(rows.size() - 1).getId() : null;
        List<ChatMessageDto> ordered = rows.stream().sorted(java.util.Comparator.comparing(ChatMessage::getId))
                .map(ChatMessageDto::fromEntity).toList();
        return new com.eduquest.ai.dto.ChatHistoryPage(ordered, next, more);
    }
}
