package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.domain.StudentXpTransaction;
import com.eduquest.repository.StudentRepository;
import com.eduquest.repository.StudentXpTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class XpService {

    private final StudentXpTransactionRepository xpTransactionRepository;
    private final StudentRepository studentRepository;
    private final BadgeService badgeService;
    private final LevelService levelService;
    private final CoinService coinService;
    private final StreakService streakService;

    public XpService(
            StudentXpTransactionRepository xpTransactionRepository,
            StudentRepository studentRepository,
            BadgeService badgeService,
            LevelService levelService,
            CoinService coinService,
            StreakService streakService) {
        this.xpTransactionRepository = xpTransactionRepository;
        this.studentRepository = studentRepository;
        this.badgeService = badgeService;
        this.levelService = levelService;
        this.coinService = coinService;
        this.streakService = streakService;
    }

    @Transactional
    public boolean awardXp(Long studentId, Long activityId, Integer xpReward, String reason) {
        if (studentId == null || xpReward == null || xpReward <= 0) {
            return false;
        }

        // If activity-based, check idempotency
        if (activityId != null && xpTransactionRepository.findByStudentIdAndActivityId(studentId, activityId).isPresent()) {
            return false; // Already awarded!
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        int oldXp = student.getXp() != null ? student.getXp() : 0;
        int newXp = oldXp + xpReward;
        int newLevel = levelService.calculateLevel(newXp);

        student.setXp(newXp);
        student.setLevel(newLevel);
        studentRepository.save(student);

        // Record XP Transaction
        StudentXpTransaction tx = StudentXpTransaction.builder()
                .studentId(studentId)
                .activityId(activityId)
                .xpAwarded(xpReward)
                .reason(reason != null ? reason : "ACTIVITY_COMPLETION")
                .createdAt(LocalDateTime.now())
                .build();
        xpTransactionRepository.save(tx);

        // Auto coin conversion (10 coins for every 100 XP threshold crossed)
        int coinsToAward = (newXp / 100) * 10 - (oldXp / 100) * 10;
        if (coinsToAward > 0) {
            coinService.awardCoins(studentId, coinsToAward, "XP_MILESTONE_CONVERSION");
        }

        // Record active streak
        streakService.recordActivity(studentId);

        // Evaluate achievements / badges
        badgeService.evaluateBadges(studentId);

        return true;
    }

    @Transactional
    public boolean awardDailyLoginXp(Long studentId) {
        if (studentId == null) return false;
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return false;

        java.time.LocalDate today = java.time.LocalDate.now();
        if (student.getLastActiveDate() != null && student.getLastActiveDate().equals(today)) {
            // Already logged in today
            return false;
        }

        return awardXp(studentId, null, 10, "DAILY_LOGIN");
    }
}
