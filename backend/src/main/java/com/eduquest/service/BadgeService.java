package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.domain.StudentBadge;
import com.eduquest.repository.StudentActivityProgressRepository;
import com.eduquest.repository.StudentBadgeRepository;
import com.eduquest.repository.StudentModuleProgressRepository;
import com.eduquest.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BadgeService {

    private final StudentBadgeRepository badgeRepository;
    private final StudentRepository studentRepository;
    private final StudentActivityProgressRepository progressRepository;
    private final StudentModuleProgressRepository moduleProgressRepository;

    public BadgeService(
            StudentBadgeRepository badgeRepository,
            StudentRepository studentRepository,
            StudentActivityProgressRepository progressRepository,
            StudentModuleProgressRepository moduleProgressRepository) {
        this.badgeRepository = badgeRepository;
        this.studentRepository = studentRepository;
        this.progressRepository = progressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
    }

    @Transactional
    public void evaluateBadges(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return;

        // 1. Learning Badges
        long completedActivitiesCount = progressRepository.countByStudentIdAndCompletedTrue(studentId);
        if (completedActivitiesCount >= 1) {
            awardBadge(studentId, "FIRST_LESSON", "First Step Achiever");
        }
        if (completedActivitiesCount >= 5) {
            awardBadge(studentId, "5_LESSONS", "Knowledge Seeker");
        }
        if (completedActivitiesCount >= 10) {
            awardBadge(studentId, "10_LESSONS", "Curious Scholar");
        }

        long completedModulesCount = moduleProgressRepository.countByStudentIdAndCompletedTrue(studentId);
        if (completedModulesCount >= 1) {
            awardBadge(studentId, "MODULE_MASTER", "Module Master");
        }
        if (completedModulesCount >= 3) {
            awardBadge(studentId, "3_MODULES", "Module Conqueror");
        }

        // 2. Consistency Badges
        int streak = student.getCurrentStreak() != null ? student.getCurrentStreak() : 0;
        if (streak >= 3) {
            awardBadge(studentId, "STREAK_3", "3-Day Fire");
        }
        if (streak >= 7) {
            awardBadge(studentId, "STREAK_7", "Week Champion");
        }
        if (streak >= 30) {
            awardBadge(studentId, "STREAK_30", "Unstoppable Scholar");
        }

        // 3. Achievement Badges
        int xp = student.getXp() != null ? student.getXp() : 0;
        if (xp >= 100) {
            awardBadge(studentId, "100_XP", "Century Scholar");
        }
        if (xp >= 500) {
            awardBadge(studentId, "500_XP", "Grandmaster Scholar");
        }
        if (xp >= 1000) {
            awardBadge(studentId, "1000_XP", "Legendary Scholar");
        }

        int level = student.getLevel() != null ? student.getLevel() : 1;
        if (level >= 5) {
            awardBadge(studentId, "LEVEL_5", "High Achiever");
        }

        int coins = student.getCoins() != null ? student.getCoins() : 0;
        if (coins >= 50) {
            awardBadge(studentId, "COIN_COLLECTOR", "Treasure Hunter");
        }
    }

    @Transactional
    public void awardBadge(Long studentId, String badgeCode, String badgeName) {
        if (badgeRepository.findByStudentIdAndBadgeCode(studentId, badgeCode).isEmpty()) {
            StudentBadge badge = StudentBadge.builder()
                    .studentId(studentId)
                    .badgeCode(badgeCode)
                    .badgeName(badgeName)
                    .earnedAt(LocalDateTime.now())
                    .build();
            badgeRepository.save(badge);
        }
    }
}
