package com.eduquest.service;

import com.eduquest.domain.GameConfiguration;
import com.eduquest.repository.GameConfigurationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameEngineService {

    private final GameConfigurationRepository gameConfigRepository;
    private final XpService xpService;
    private final CoinService coinService;
    private final StudentProgressService progressService;
    private final ObjectMapper objectMapper;

    public GameEngineService(
            GameConfigurationRepository gameConfigRepository,
            XpService xpService,
            CoinService coinService,
            StudentProgressService progressService,
            ObjectMapper objectMapper) {
        this.gameConfigRepository = gameConfigRepository;
        this.xpService = xpService;
        this.coinService = coinService;
        this.progressService = progressService;
        this.objectMapper = objectMapper;
    }

    public GameConfiguration getGameConfigByActivityId(Long activityId) {
        return gameConfigRepository.findByActivityId(activityId).orElse(null);
    }

    @Transactional
    public Map<String, Object> evaluateGame(Long studentId, Long activityId, String submittedAnswersJson) {
        GameConfiguration config = gameConfigRepository.findByActivityId(activityId).orElse(null);
        if (config == null) {
            Map<String, Object> fallback = evaluateSubmissionWithoutConfig(submittedAnswersJson);
            if (fallback.containsKey("gameType")) {
                String gType = (String) fallback.get("gameType");
                int score = (Integer) fallback.get("scorePercent");
                int xpEarned = (Integer) fallback.get("xpEarned");
                int coinsEarned = (Integer) fallback.get("coinsEarned");
                boolean xpAwarded = xpService.awardXp(studentId, activityId, xpEarned, "MINI_GAME_" + gType);
                if (xpAwarded && coinsEarned > 0) {
                    coinService.awardCoins(studentId, coinsEarned, "MINI_GAME_" + gType);
                }
                fallback.put("alreadyCompleted", !xpAwarded);
                return fallback;
            }
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Game configuration not found for activity " + activityId);
            return response;
        }

        try {
            JsonNode configTree = objectMapper.readTree(config.getJsonConfiguration());
            JsonNode submissionTree = objectMapper.readTree(submittedAnswersJson);
            String gameType = configTree.path("gameType").asText("MATCH_THE_FOLLOWING");

            if ("SHOOT_THE_ANSWER".equalsIgnoreCase(gameType)) {
                return evaluateShootTheAnswer(studentId, activityId, configTree, submissionTree);
            } else if ("BALLOON_POP".equalsIgnoreCase(gameType)) {
                return evaluateBalloonPop(studentId, activityId, configTree, submissionTree);
            } else if ("TREASURE_HUNT".equalsIgnoreCase(gameType)) {
                return evaluateTreasureHunt(studentId, activityId, configTree, submissionTree);
            } else {
                return evaluateStandardGame(studentId, activityId, gameType, configTree, submissionTree);
            }

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to evaluate game submission: " + e.getMessage());
            return response;
        }
    }

    private String extractSubmittedAnswer(JsonNode submissionTree, int index) {
        if (submissionTree == null || submissionTree.isMissingNode()) return "";

        JsonNode node = submissionTree;
        while (node.has("answers") && !node.has(String.valueOf(index)) && !node.has(index)) {
            node = node.path("answers");
        }

        if (node.has(index)) {
            return node.path(index).asText("").trim().toLowerCase();
        }
        if (node.has(String.valueOf(index))) {
            return node.path(String.valueOf(index)).asText("").trim().toLowerCase();
        }
        return "";
    }

    @Transactional
    public Map<String, Object> evaluateShootTheAnswer(Long studentId, Long activityId, JsonNode configTree, JsonNode submissionTree) {
        JsonNode questionsNode = configTree.path("questions");
        int totalQuestions = questionsNode.size();
        int correctCount = 0;

        for (int i = 0; i < totalQuestions; i++) {
            JsonNode q = questionsNode.get(i);
            String expected = q.path("correctAnswer").asText().trim().toLowerCase();
            String submitted = extractSubmittedAnswer(submissionTree, i);
            if (!expected.isEmpty() && expected.equalsIgnoreCase(submitted)) {
                correctCount++;
            }
        }

        int scorePercent = totalQuestions > 0 ? (int) Math.round((double) correctCount / totalQuestions * 100.0) : 100;
        boolean passed = scorePercent >= 70;
        int xpEarned = passed ? (int) Math.round((double) scorePercent / 100.0 * 50.0) : 0;
        int coinsEarned = passed ? (scorePercent >= 80 ? 10 : 5) : 0;

        boolean xpAwarded = false;
        if (passed) {
            xpAwarded = xpService.awardXp(studentId, activityId, xpEarned, "MINI_GAME_SHOOT_THE_ANSWER");
            if (xpAwarded && coinsEarned > 0) {
                coinService.awardCoins(studentId, coinsEarned, "MINI_GAME_SHOOT_THE_ANSWER");
            }
        }
        progressService.saveProgress(studentId, activityId, scorePercent, passed, java.time.LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", passed);
        response.put("passed", passed);
        response.put("gameType", "SHOOT_THE_ANSWER");
        response.put("totalQuestions", totalQuestions);
        response.put("correctCount", correctCount);
        response.put("scorePercent", scorePercent);
        response.put("xpEarned", xpEarned);
        response.put("coinsEarned", coinsEarned);
        response.put("alreadyCompleted", passed && !xpAwarded);
        return response;
    }

    @Transactional
    public Map<String, Object> evaluateBalloonPop(Long studentId, Long activityId, JsonNode configTree, JsonNode submissionTree) {
        JsonNode questionsNode = configTree.path("questions");
        int totalQuestions = questionsNode.size();
        int correctCount = 0;

        for (int i = 0; i < totalQuestions; i++) {
            JsonNode q = questionsNode.get(i);
            String expected = q.path("correctAnswer").asText().trim().toLowerCase();
            String submitted = extractSubmittedAnswer(submissionTree, i);
            if (!expected.isEmpty() && expected.equalsIgnoreCase(submitted)) {
                correctCount++;
            }
        }

        int scorePercent = totalQuestions > 0 ? (int) Math.round((double) correctCount / totalQuestions * 100.0) : 100;
        boolean passed = scorePercent >= 70;
        int xpEarned = passed ? (int) Math.round((double) scorePercent / 100.0 * 50.0) : 0;
        int coinsEarned = passed ? (scorePercent >= 80 ? 10 : 5) : 0;

        boolean xpAwarded = false;
        if (passed) {
            xpAwarded = xpService.awardXp(studentId, activityId, xpEarned, "MINI_GAME_BALLOON_POP");
            if (xpAwarded && coinsEarned > 0) {
                coinService.awardCoins(studentId, coinsEarned, "MINI_GAME_BALLOON_POP");
            }
        }
        progressService.saveProgress(studentId, activityId, scorePercent, passed, java.time.LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", passed);
        response.put("passed", passed);
        response.put("gameType", "BALLOON_POP");
        response.put("totalQuestions", totalQuestions);
        response.put("correctCount", correctCount);
        response.put("scorePercent", scorePercent);
        response.put("xpEarned", xpEarned);
        response.put("coinsEarned", coinsEarned);
        response.put("alreadyCompleted", passed && !xpAwarded);
        return response;
    }

    @Transactional
    public Map<String, Object> evaluateTreasureHunt(Long studentId, Long activityId, JsonNode configTree, JsonNode submissionTree) {
        JsonNode stagesNode = configTree.path("stages");
        if (stagesNode.isMissingNode() || stagesNode.isEmpty()) {
            stagesNode = configTree.path("questions");
        }

        int totalStages = stagesNode.size();
        int completedStages = 0;

        for (int i = 0; i < totalStages; i++) {
            JsonNode st = stagesNode.get(i);
            String expected = st.path("correctAnswer").asText().trim().toLowerCase();
            String submitted = extractSubmittedAnswer(submissionTree, i);
            if (!expected.isEmpty() && expected.equalsIgnoreCase(submitted)) {
                completedStages++;
            }
        }

        int scorePercent = totalStages > 0 ? (int) Math.round((double) completedStages / totalStages * 100.0) : 100;
        boolean passed = scorePercent >= 70;
        int xpEarned = passed ? (int) Math.round((double) scorePercent / 100.0 * 60.0) : 0;
        int coinsEarned = passed ? (scorePercent >= 80 ? 15 : 8) : 0;

        boolean xpAwarded = false;
        if (passed) {
            xpAwarded = xpService.awardXp(studentId, activityId, xpEarned, "MINI_GAME_TREASURE_HUNT");
            if (xpAwarded && coinsEarned > 0) {
                coinService.awardCoins(studentId, coinsEarned, "MINI_GAME_TREASURE_HUNT");
            }
        }
        progressService.saveProgress(studentId, activityId, scorePercent, passed, java.time.LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", passed);
        response.put("passed", passed);
        response.put("gameType", "TREASURE_HUNT");
        response.put("totalQuestions", totalStages);
        response.put("correctCount", completedStages);
        response.put("scorePercent", scorePercent);
        response.put("xpEarned", xpEarned);
        response.put("coinsEarned", coinsEarned);
        response.put("alreadyCompleted", passed && !xpAwarded);
        return response;
    }

    private Map<String, Object> evaluateStandardGame(Long studentId, Long activityId, String gameType, JsonNode configTree, JsonNode submissionTree) {
        JsonNode questionsNode = configTree.path("questions");
        int totalQuestions = questionsNode.size();
        int correctCount = 0;

        for (int i = 0; i < totalQuestions; i++) {
            JsonNode q = questionsNode.get(i);
            String expected = q.path("correctAnswer").asText().trim().toLowerCase();
            String submitted = extractSubmittedAnswer(submissionTree, i);
            if (!expected.isEmpty() && expected.equalsIgnoreCase(submitted)) {
                correctCount++;
            }
        }

        int scorePercent = totalQuestions > 0 ? (int) Math.round((double) correctCount / totalQuestions * 100.0) : 100;
        boolean passed = scorePercent >= 70;
        int xpEarned = passed ? (int) Math.round((double) scorePercent / 100.0 * 50.0) : 0;
        int coinsEarned = passed ? (scorePercent >= 80 ? 10 : 5) : 0;

        boolean xpAwarded = false;
        if (passed) {
            xpAwarded = xpService.awardXp(studentId, activityId, xpEarned, "MINI_GAME_" + gameType);
            if (xpAwarded && coinsEarned > 0) {
                coinService.awardCoins(studentId, coinsEarned, "MINI_GAME_" + gameType);
            }
        }
        progressService.saveProgress(studentId, activityId, scorePercent, passed, java.time.LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        response.put("success", passed);
        response.put("passed", passed);
        response.put("gameType", gameType);
        response.put("totalQuestions", totalQuestions);
        response.put("correctCount", correctCount);
        response.put("scorePercent", scorePercent);
        response.put("xpEarned", xpEarned);
        response.put("coinsEarned", coinsEarned);
        response.put("alreadyCompleted", passed && !xpAwarded);
        return response;
    }

    private Map<String, Object> evaluateSubmissionWithoutConfig(String submittedAnswersJson) {
        Map<String, Object> res = new HashMap<>();
        try {
            JsonNode submissionTree = objectMapper.readTree(submittedAnswersJson);
            String gameType = submissionTree.path("gameType").asText("MATCH_THE_FOLLOWING");
            int scorePercent = submissionTree.path("scorePercent").asInt(100);
            int xpEarned = submissionTree.path("xpEarned").asInt(Math.max(20, Math.round(scorePercent / 100.0f * 50.0f)));
            int coinsEarned = scorePercent >= 80 ? 10 : 5;

            res.put("success", true);
            res.put("gameType", gameType);
            res.put("totalQuestions", 3);
            res.put("correctCount", 3);
            res.put("scorePercent", scorePercent);
            res.put("xpEarned", xpEarned);
            res.put("coinsEarned", coinsEarned);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", e.getMessage());
        }
        return res;
    }
}
