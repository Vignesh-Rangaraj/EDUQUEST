package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.domain.StudentXpTransaction;
import com.eduquest.repository.StudentRepository;
import com.eduquest.repository.StudentXpTransactionRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class StreakService {

    private final StudentRepository studentRepository;
    private final StudentXpTransactionRepository xpTransactionRepository;
    private final LevelService levelService;
    private final CoinService coinService;

    public StreakService(
            StudentRepository studentRepository,
            StudentXpTransactionRepository xpTransactionRepository,
            LevelService levelService,
            CoinService coinService) {
        this.studentRepository = studentRepository;
        this.xpTransactionRepository = xpTransactionRepository;
        this.levelService = levelService;
        this.coinService = coinService;
    }

    @Transactional
    public void recordActivity(Long studentId) {
        if (studentId == null) return;
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return;

        LocalDate today = LocalDate.now();
        LocalDate lastActive = student.getLastActiveDate();

        if (lastActive == null) {
            student.setCurrentStreak(1);
            student.setHighestStreak(1);
            student.setLastActiveDate(today);
            studentRepository.save(student);
        } else if (lastActive.equals(today.minusDays(1))) {
            int newStreak = (student.getCurrentStreak() != null ? student.getCurrentStreak() : 0) + 1;
            student.setCurrentStreak(newStreak);
            int highest = student.getHighestStreak() != null ? student.getHighestStreak() : 0;
            if (newStreak > highest) {
                student.setHighestStreak(newStreak);
            }
            student.setLastActiveDate(today);
            studentRepository.save(student);

            // Award streak milestone bonuses
            if (newStreak == 7) {
                awardBonusXp(student, 100, "7_DAY_STREAK_BONUS");
            } else if (newStreak == 30) {
                awardBonusXp(student, 500, "30_DAY_STREAK_BONUS");
            }
        } else if (!lastActive.equals(today)) {
            int highest = student.getHighestStreak() != null ? student.getHighestStreak() : 1;
            int curr = student.getCurrentStreak() != null ? student.getCurrentStreak() : 1;
            int effective = Math.max(curr, highest);
            student.setCurrentStreak(effective);
            student.setHighestStreak(effective);
            student.setLastActiveDate(today);
            studentRepository.save(student);
        }
    }

    private void awardBonusXp(Student student, int bonusXp, String reason) {
        int oldXp = student.getXp() != null ? student.getXp() : 0;
        int newXp = oldXp + bonusXp;
        student.setXp(newXp);
        student.setLevel(levelService.calculateLevel(newXp));
        studentRepository.save(student);

        StudentXpTransaction tx = StudentXpTransaction.builder()
                .studentId(student.getId())
                .xpAwarded(bonusXp)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        xpTransactionRepository.save(tx);

        // Auto coin conversion (10 coins per 100 XP earned)
        int coinsToAward = (newXp / 100) * 10 - (oldXp / 100) * 10;
        if (coinsToAward > 0) {
            coinService.awardCoins(student.getId(), coinsToAward, "XP_MILESTONE_CONVERSION");
        }
    }
}
