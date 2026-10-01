package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.domain.StudentChallengeProgress;
import com.eduquest.domain.TeacherChallenge;
import com.eduquest.repository.StudentActivityProgressRepository;
import com.eduquest.repository.StudentChallengeProgressRepository;
import com.eduquest.repository.StudentRepository;
import com.eduquest.repository.TeacherChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeacherChallengeService {

    private final TeacherChallengeRepository challengeRepository;
    private final StudentChallengeProgressRepository progressRepository;
    private final StudentRepository studentRepository;
    private final StudentActivityProgressRepository activityProgressRepository;
    private final XpService xpService;
    private final CoinService coinService;
    private final BadgeService badgeService;

    public TeacherChallengeService(
            TeacherChallengeRepository challengeRepository,
            StudentChallengeProgressRepository progressRepository,
            StudentRepository studentRepository,
            StudentActivityProgressRepository activityProgressRepository,
            XpService xpService,
            CoinService coinService,
            BadgeService badgeService) {
        this.challengeRepository = challengeRepository;
        this.progressRepository = progressRepository;
        this.studentRepository = studentRepository;
        this.activityProgressRepository = activityProgressRepository;
        this.xpService = xpService;
        this.coinService = coinService;
        this.badgeService = badgeService;
    }

    @Transactional
    public TeacherChallenge createChallenge(TeacherChallenge challenge) {
        if (challenge.getStartDate() == null) challenge.setStartDate(LocalDate.now());
        if (challenge.getEndDate() == null) challenge.setEndDate(LocalDate.now().plusDays(7));
        if (challenge.getArchived() == null) challenge.setArchived(false);
        return challengeRepository.save(challenge);
    }

    public List<TeacherChallenge> getChallengesByClassroom(Long classroomId) {
        return challengeRepository.findByClassroomId(classroomId);
    }

    public List<TeacherChallenge> getActiveChallengesByClassroom(Long classroomId) {
        LocalDate now = LocalDate.now();
        return challengeRepository.findByClassroomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndArchivedFalse(classroomId, now, now);
    }

    public List<TeacherChallenge> getChallengesByTeacher(Long teacherId) {
        return challengeRepository.findByTeacherId(teacherId);
    }

    @Transactional
    public TeacherChallenge updateChallenge(Long challengeId, TeacherChallenge updated, Long requesterTeacherId, boolean isAdmin) {
        TeacherChallenge existing = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found: " + challengeId));

        if (!isAdmin && !existing.getTeacherId().equals(requesterTeacherId)) {
            throw new RuntimeException("Unauthorized: Only creator teacher can update this challenge");
        }

        if (Boolean.TRUE.equals(existing.getArchived())) {
            throw new RuntimeException("Archived challenges cannot be edited");
        }

        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getTargetType() != null) existing.setTargetType(updated.getTargetType());
        if (updated.getTargetValue() != null) existing.setTargetValue(updated.getTargetValue());
        if (updated.getXpReward() != null) existing.setXpReward(updated.getXpReward());
        if (updated.getCoinReward() != null) existing.setCoinReward(updated.getCoinReward());
        if (updated.getBadgeRewardCode() != null) existing.setBadgeRewardCode(updated.getBadgeRewardCode());
        if (updated.getStartDate() != null) existing.setStartDate(updated.getStartDate());
        if (updated.getEndDate() != null) existing.setEndDate(updated.getEndDate());

        return challengeRepository.save(existing);
    }

    @Transactional
    public void deleteChallenge(Long challengeId, Long requesterTeacherId, boolean isAdmin) {
        TeacherChallenge existing = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found: " + challengeId));

        if (!isAdmin && !existing.getTeacherId().equals(requesterTeacherId)) {
            throw new RuntimeException("Unauthorized: Only creator teacher can delete this challenge");
        }

        List<StudentChallengeProgress> progressList = progressRepository.findByChallengeId(challengeId);
        boolean hasCompleted = progressList.stream().anyMatch(p -> Boolean.TRUE.equals(p.getCompleted()));
        if (hasCompleted) {
            throw new RuntimeException("Completed challenges cannot be deleted");
        }

        progressRepository.deleteAll(progressList);
        challengeRepository.delete(existing);
    }

    @Transactional
    public TeacherChallenge archiveChallenge(Long challengeId, Long requesterTeacherId, boolean isAdmin) {
        TeacherChallenge existing = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found: " + challengeId));

        if (!isAdmin && !existing.getTeacherId().equals(requesterTeacherId)) {
            throw new RuntimeException("Unauthorized: Only creator teacher can archive this challenge");
        }

        existing.setArchived(true);
        return challengeRepository.save(existing);
    }

    @Transactional
    public TeacherChallenge restoreChallenge(Long challengeId, Long requesterTeacherId, boolean isAdmin) {
        TeacherChallenge existing = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found: " + challengeId));

        if (!isAdmin && !existing.getTeacherId().equals(requesterTeacherId)) {
            throw new RuntimeException("Unauthorized: Only creator teacher can restore this challenge");
        }

        existing.setArchived(false);
        return challengeRepository.save(existing);
    }

    @Transactional
    public List<Map<String, Object>> getStudentChallenges(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null || student.getClassroom() == null) return List.of();

        List<TeacherChallenge> challenges = getActiveChallengesByClassroom(student.getClassroom().getId());
        long lessonsCount = activityProgressRepository.countByStudentIdAndCompletedTrue(studentId);

        return challenges.stream().map(c -> {
            StudentChallengeProgress scp = progressRepository.findByChallengeIdAndStudentId(c.getId(), studentId)
                    .orElseGet(() -> {
                        StudentChallengeProgress p = StudentChallengeProgress.builder()
                                .challengeId(c.getId())
                                .studentId(studentId)
                                .currentProgress(0)
                                .completed(false)
                                .claimed(false)
                                .build();
                        return progressRepository.save(p);
                    });

            int prog = 0;
            if ("LESSONS_COMPLETED".equalsIgnoreCase(c.getTargetType())) {
                prog = (int) Math.min(lessonsCount, c.getTargetValue());
            } else if ("STREAK_DAYS".equalsIgnoreCase(c.getTargetType())) {
                prog = Math.min(student.getCurrentStreak() != null ? student.getCurrentStreak() : 0, c.getTargetValue());
            } else {
                prog = Math.min(student.getXp() != null ? student.getXp() : 0, c.getTargetValue());
            }

            scp.setCurrentProgress(prog);
            if (prog >= c.getTargetValue()) {
                scp.setCompleted(true);
                if (scp.getCompletedAt() == null) scp.setCompletedAt(LocalDateTime.now());
            }
            progressRepository.save(scp);

            Map<String, Object> item = new HashMap<>();
            item.put("challenge", c);
            item.put("studentProgress", scp);
            return item;
        }).collect(Collectors.toList());
    }

    @Transactional
    public boolean claimChallengeReward(Long studentId, Long challengeId) {
        StudentChallengeProgress scp = progressRepository.findByChallengeIdAndStudentId(challengeId, studentId).orElse(null);
        TeacherChallenge challenge = challengeRepository.findById(challengeId).orElse(null);

        if (scp == null || challenge == null || !scp.getCompleted() || scp.getClaimed()) {
            return false;
        }

        scp.setClaimed(true);
        progressRepository.save(scp);

        if (challenge.getXpReward() != null && challenge.getXpReward() > 0) {
            xpService.awardXp(studentId, null, challenge.getXpReward(), "TEACHER_CHALLENGE_" + challengeId);
        }
        if (challenge.getCoinReward() != null && challenge.getCoinReward() > 0) {
            coinService.awardCoins(studentId, challenge.getCoinReward(), "TEACHER_CHALLENGE_" + challengeId);
        }
        if (challenge.getBadgeRewardCode() != null && !challenge.getBadgeRewardCode().isBlank()) {
            badgeService.awardBadge(studentId, challenge.getBadgeRewardCode(), challenge.getTitle() + " Master");
        }

        return true;
    }

    public Map<String, Object> getTeacherChallengeStats(Long teacherId) {
        List<TeacherChallenge> list = challengeRepository.findByTeacherId(teacherId);
        long total = list.size();
        long active = list.stream().filter(c -> !Boolean.TRUE.equals(c.getArchived()) && !c.getEndDate().isBefore(LocalDate.now())).count();
        long archived = list.stream().filter(c -> Boolean.TRUE.equals(c.getArchived())).count();

        List<Long> cIds = list.stream().map(TeacherChallenge::getId).toList();
        long completedCount = 0;
        for (Long id : cIds) {
            completedCount += progressRepository.findByChallengeId(id).stream().filter(p -> Boolean.TRUE.equals(p.getCompleted())).count();
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalChallenges", total);
        stats.put("activeChallenges", active);
        stats.put("archivedChallenges", archived);
        stats.put("completedChallenges", completedCount);
        return stats;
    }

    public Map<String, Object> getAdminChallengeStats() {
        List<TeacherChallenge> list = challengeRepository.findAll();
        long total = list.size();
        long archived = list.stream().filter(c -> Boolean.TRUE.equals(c.getArchived())).count();

        long totalProgressRecords = progressRepository.count();
        long completedProgressRecords = progressRepository.findAll().stream().filter(p -> Boolean.TRUE.equals(p.getCompleted())).count();

        double completionRate = totalProgressRecords > 0 ? (double) completedProgressRecords / totalProgressRecords * 100.0 : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlatformChallenges", total);
        stats.put("archivedChallenges", archived);
        stats.put("challengeCompletionRate", Math.round(completionRate * 10.0) / 10.0);
        return stats;
    }
}
