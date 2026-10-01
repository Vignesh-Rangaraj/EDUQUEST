package com.eduquest.service;

import com.eduquest.domain.*;
import com.eduquest.dto.QuizQuestionDto;
import com.eduquest.dto.SubmitQuizRequest;
import com.eduquest.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final StudentQuizAttemptRepository attemptRepository;
    private final ActivityRepository activityRepository;
    private final StudentActivityProgressRepository progressRepository;
    private final ActivityCompletionService completionService;
    private final XpService xpService;
    private final StudentModuleProgressService moduleProgressService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuizService(
            QuizQuestionRepository quizQuestionRepository,
            StudentQuizAttemptRepository attemptRepository,
            ActivityRepository activityRepository,
            StudentActivityProgressRepository progressRepository,
            ActivityCompletionService completionService,
            XpService xpService,
            StudentModuleProgressService moduleProgressService) {
        this.quizQuestionRepository = quizQuestionRepository;
        this.attemptRepository = attemptRepository;
        this.activityRepository = activityRepository;
        this.progressRepository = progressRepository;
        this.completionService = completionService;
        this.xpService = xpService;
        this.moduleProgressService = moduleProgressService;
    }

    @Transactional(readOnly = true)
    public List<QuizQuestionDto> getQuestionsForActivity(Long activityId) {
        return quizQuestionRepository.findByActivityIdOrderByDisplayOrderAsc(activityId).stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuizQuestionDto saveQuestion(Long activityId, QuizQuestionDto dto) {
        QuizQuestion qq = QuizQuestion.builder()
                .activityId(activityId)
                .questionText(dto.getQuestionText())
                .optionA(dto.getOptionA())
                .optionB(dto.getOptionB())
                .optionC(dto.getOptionC())
                .optionD(dto.getOptionD())
                .correctAnswer(dto.getCorrectAnswer())
                .explanation(dto.getExplanation())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 1)
                .build();
        QuizQuestion saved = quizQuestionRepository.save(qq);
        return QuizQuestionDto.fromEntity(saved);
    }

    @Transactional
    public StudentQuizAttempt submitQuiz(Long studentId, SubmitQuizRequest request) {
        Long activityId = request.getActivityId();
        List<QuizQuestion> questions = quizQuestionRepository.findByActivityIdOrderByDisplayOrderAsc(activityId);
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        Map<Long, String> userAnswers = request.getUserAnswers();
        for (QuizQuestion q : questions) {
            String submitted = userAnswers.get(q.getId());
            if (submitted != null && submitted.trim().equalsIgnoreCase(q.getCorrectAnswer().trim())) {
                correctAnswers++;
            }
        }

        int score = totalQuestions > 0 ? (int) Math.round(((double) correctAnswers / totalQuestions) * 100.0) : 0;

        String answersJson = "{}";
        try {
            answersJson = objectMapper.writeValueAsString(userAnswers);
        } catch (Exception e) {
            answersJson = "{}";
        }

        StudentQuizAttempt attempt = StudentQuizAttempt.builder()
                .studentId(studentId)
                .activityId(activityId)
                .score(score)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .answersJson(answersJson)
                .completedAt(LocalDateTime.now())
                .build();
        attemptRepository.save(attempt);

        Activity activity = activityRepository.findById(activityId).orElse(null);
        boolean isPassed = completionService.isActivityCompleted(activity, score, true);

        // Update StudentActivityProgress
        StudentActivityProgress progress = progressRepository.findByStudentIdAndActivityId(studentId, activityId)
                .orElseGet(() -> StudentActivityProgress.builder().studentId(studentId).activityId(activityId).score(0).bestScore(0).attemptCount(0).completed(false).build());

        progress.setAttemptCount(progress.getAttemptCount() + 1);
        if (score > progress.getBestScore()) {
            progress.setBestScore(score);
        }
        progress.setScore(score);

        if (isPassed) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            
            // Award XP once
            int xpReward = (activity != null && activity.getXpReward() != null) ? activity.getXpReward() : 20;
            xpService.awardXp(studentId, activityId, xpReward, "QUIZ_COMPLETION");

            // Recalculate module progress
            if (activity != null && activity.getModuleId() != null) {
                moduleProgressService.updateModuleProgress(studentId, activity.getModuleId());
            }
        }

        progressRepository.save(progress);
        return attempt;
    }
}
