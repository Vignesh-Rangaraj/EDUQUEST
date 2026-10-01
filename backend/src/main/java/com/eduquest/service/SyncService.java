package com.eduquest.service;

import com.eduquest.domain.*;
import com.eduquest.dto.SyncItemDto;
import com.eduquest.dto.SyncRequestDto;
import com.eduquest.dto.SyncResponse;
import com.eduquest.repository.ActivityRepository;
import com.eduquest.repository.SyncQueueRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SyncService {

    private final SyncQueueRepository syncQueueRepository;
    private final StudentProgressService progressService;
    private final ActivityRepository activityRepository;
    private final XpService xpService;
    private final StudentModuleProgressService moduleProgressService;
    private final ActivityCompletionService completionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SyncService(
            SyncQueueRepository syncQueueRepository,
            StudentProgressService progressService,
            ActivityRepository activityRepository,
            XpService xpService,
            StudentModuleProgressService moduleProgressService,
            ActivityCompletionService completionService) {
        this.syncQueueRepository = syncQueueRepository;
        this.progressService = progressService;
        this.activityRepository = activityRepository;
        this.xpService = xpService;
        this.moduleProgressService = moduleProgressService;
        this.completionService = completionService;
    }

    @Transactional
    public SyncResponse processSync(SyncRequestDto request) {
        List<String> syncedItems = new ArrayList<>();
        List<String> failedItems = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return new SyncResponse(true, syncedItems, failedItems, errors);
        }

        for (SyncItemDto item : request.getItems()) {
            String clientItemId = item.getId();

            // Idempotency check: Client queue item ID deduplication
            if (clientItemId != null && !clientItemId.trim().isEmpty()) {
                Optional<SyncQueue> existingEntry = syncQueueRepository.findFirstByClientQueueItemId(clientItemId);
                if (existingEntry.isPresent() && existingEntry.get().getStatus() == SyncStatus.COMPLETED) {
                    syncedItems.add(clientItemId);
                    continue;
                }
            }

            SyncQueue queueEntry = SyncQueue.builder()
                    .clientQueueItemId(clientItemId)
                    .studentId(request.getStudentId() != null ? request.getStudentId() : item.getStudentId())
                    .actionType(item.getActionType())
                    .payloadJson(item.getPayload() != null ? item.getPayload() : "{}")
                    .status(SyncStatus.PROCESSING)
                    .build();
            syncQueueRepository.save(queueEntry);

            try {
                if ("COMPLETE_ACTIVITY".equalsIgnoreCase(item.getActionType()) || "UPDATE_PROGRESS".equalsIgnoreCase(item.getActionType())) {
                    Long studentId = request.getStudentId() != null ? request.getStudentId() : item.getStudentId();
                    Long activityId = item.getActivityId();
                    Integer score = item.getScore();
                    String completedAtStr = item.getCompletedAt();

                    // Parse JSON payload if fields missing at top level
                    if ((activityId == null || score == null || studentId == null) && item.getPayload() != null) {
                        try {
                            JsonNode json = objectMapper.readTree(item.getPayload());
                            if (studentId == null && json.has("studentId")) studentId = json.get("studentId").asLong();
                            if (activityId == null && json.has("activityId")) activityId = json.get("activityId").asLong();
                            if (score == null && json.has("score")) score = json.get("score").asInt();
                            if (completedAtStr == null && json.has("completedAt")) completedAtStr = json.get("completedAt").asText();
                        } catch (Exception ignored) {}
                    }

                    if (score == null) score = 100;
                    LocalDateTime completedAt = parseDateTime(completedAtStr);

                    if (studentId == null || activityId == null) throw new IllegalArgumentException("Student and activity are required.");
                    Activity activity = activityRepository.findById(activityId).orElse(null);
                    if (activity == null) throw new IllegalArgumentException("Activity not found: " + activityId);
                    boolean isCompleted = completionService.isActivityCompleted(activity, score, true);
                    progressService.saveProgress(studentId, activityId, score, isCompleted, completedAt);

                    if (isCompleted) {
                        int xpReward = activity.getXpReward() != null ? activity.getXpReward() : 10;
                        xpService.awardXp(studentId, activityId, xpReward, "ACTIVITY_COMPLETION");
                        if (activity.getModuleId() != null) moduleProgressService.updateModuleProgress(studentId, activity.getModuleId());
                    }
                } else if ("EARN_XP".equalsIgnoreCase(item.getActionType())) {
                    Long studentId = request.getStudentId() != null ? request.getStudentId() : item.getStudentId();
                    Long activityId = item.getActivityId();
                    if (activityId == null && item.getPayload() != null) {
                        try { activityId = objectMapper.readTree(item.getPayload()).path("activityId").asLong(); }
                        catch (Exception ignored) { }
                    }
                    if (studentId == null || activityId == null) throw new IllegalArgumentException("Student and activity are required for XP sync.");
                    Activity activity = activityRepository.findById(activityId).orElse(null);
                    if (activity == null) throw new IllegalArgumentException("Activity not found: " + activityId);
                    int xpReward = activity.getXpReward() != null ? activity.getXpReward() : 10;
                    xpService.awardXp(studentId, activityId, xpReward, "OFFLINE_ACTIVITY_COMPLETION");
                } else {
                    throw new IllegalArgumentException("Unsupported sync action: " + item.getActionType());
                }

                queueEntry.setStatus(SyncStatus.COMPLETED);
                queueEntry.setProcessedAt(LocalDateTime.now());
                syncQueueRepository.save(queueEntry);

                syncedItems.add(clientItemId != null ? clientItemId : String.valueOf(queueEntry.getId()));
            } catch (Exception e) {
                queueEntry.setStatus(SyncStatus.FAILED);
                queueEntry.setProcessedAt(LocalDateTime.now());
                syncQueueRepository.save(queueEntry);

                failedItems.add(clientItemId != null ? clientItemId : String.valueOf(queueEntry.getId()));
                errors.add("Failed item " + clientItemId + ": " + e.getMessage());
            }
        }

        boolean overallSuccess = failedItems.isEmpty();
        return new SyncResponse(overallSuccess, syncedItems, failedItems, errors);
    }

    private LocalDateTime parseDateTime(String str) {
        if (str == null || str.trim().isEmpty()) return LocalDateTime.now();
        try {
            return LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
