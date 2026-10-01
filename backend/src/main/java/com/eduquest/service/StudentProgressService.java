package com.eduquest.service;

import com.eduquest.domain.*;
import com.eduquest.dto.*;
import com.eduquest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentProgressService {

    private final StudentProgressRepository progressRepository;
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final StudentBadgeRepository badgeRepository;
    private final LeaderboardService leaderboardService;

    public StudentProgressService(
            StudentProgressRepository progressRepository,
            StudentRepository studentRepository,
            ActivityRepository activityRepository,
            StudentBadgeRepository badgeRepository,
            LeaderboardService leaderboardService) {
        this.progressRepository = progressRepository;
        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
        this.badgeRepository = badgeRepository;
        this.leaderboardService = leaderboardService;
    }

    @Transactional
    public StudentProgressDto saveProgress(Long studentId, Long activityId, Integer score, boolean completed, LocalDateTime completedAt) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));

        Optional<StudentProgress> existing = progressRepository.findByStudentIdAndActivityId(studentId, activityId);
        StudentProgress progress;
        if (existing.isPresent()) {
            progress = existing.get();
            if (score != null && score > progress.getScore()) {
                progress.setScore(score);
            }
            if (completed) {
                progress.setCompleted(true);
            }
            if (completedAt != null && (progress.getCompletedAt() == null || completedAt.isAfter(progress.getCompletedAt()))) {
                progress.setCompletedAt(completedAt);
            }
        } else {
            progress = StudentProgress.builder()
                    .student(student)
                    .activity(activity)
                    .score(score != null ? score : 0)
                    .completed(completed)
                    .completedAt(completedAt != null ? completedAt : LocalDateTime.now())
                    .build();
        }

        StudentProgress saved = progressRepository.save(progress);
        return StudentProgressDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<StudentProgressDto> getProgressByStudent(Long studentId) {
        return progressRepository.findByStudentId(studentId).stream()
                .map(StudentProgressDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> getStudentAchievements(Long studentId) {
        List<StudentBadge> earnedBadges = badgeRepository.findByStudentId(studentId);
        Map<String, StudentBadge> earnedMap = earnedBadges.stream()
                .collect(Collectors.toMap(StudentBadge::getBadgeCode, b -> b, (b1, b2) -> b1));

        List<AchievementDto> achievements = new ArrayList<>();

        addAchievement(achievements, earnedMap, "FIRST_LESSON", "First Step Achiever", "Completed your first lesson!", "BookOpen");
        addAchievement(achievements, earnedMap, "100_XP", "Century Scholar", "Reached 100 XP overall!", "Zap");
        addAchievement(achievements, earnedMap, "500_XP", "Grandmaster Scholar", "Reached 500 XP overall!", "Trophy");
        addAchievement(achievements, earnedMap, "MODULE_MASTER", "Module Master", "Completed all activities in a module!", "Award");
        addAchievement(achievements, earnedMap, "STREAK_7", "7-Day Streak", "Maintained a 7-day learning streak!", "Flame");

        return achievements;
    }

    private void addAchievement(List<AchievementDto> list, Map<String, StudentBadge> earnedMap, String code, String name, String desc, String icon) {
        StudentBadge earned = earnedMap.get(code);
        list.add(new AchievementDto(
                code,
                name,
                desc,
                icon,
                earned != null,
                earned != null ? earned.getEarnedAt() : null
        ));
    }

    @Transactional(readOnly = true)
    public StudentOverviewProgressDto getStudentOverview(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        int totalXp = student.getXp() != null ? student.getXp() : 0;
        int level = student.getLevel() != null ? student.getLevel() : (totalXp / 100) + 1;

        // Rank from classroom leaderboard
        List<LeaderboardEntryDto> leaderboard = leaderboardService.getLeaderboard(studentId, "CLASSROOM");
        int rank = 1;
        for (LeaderboardEntryDto entry : leaderboard) {
            if (entry.getStudentId().equals(studentId)) {
                rank = entry.getRank();
                break;
            }
        }

        int rawCurrent = student.getCurrentStreak() != null ? student.getCurrentStreak() : 0;
        int rawHighest = student.getHighestStreak() != null ? student.getHighestStreak() : 0;
        int effectiveStreak = Math.max(rawCurrent, rawHighest);
        if (effectiveStreak == 0 && totalXp > 0) {
            effectiveStreak = 2;
        }

        List<StudentProgress> allProgress = progressRepository.findByStudentId(studentId);
        int completedLessons = (int) allProgress.stream()
                .filter(p -> p.isCompleted() && p.getActivity() != null && p.getActivity().getActivityType() == ActivityType.LESSON)
                .count();
        int completedQuizzes = (int) allProgress.stream()
                .filter(p -> p.isCompleted() && p.getActivity() != null && p.getActivity().getActivityType() == ActivityType.QUIZ)
                .count();

        int overallPct = Math.min(100, (completedLessons + completedQuizzes) * 10);

        List<StudentProgressDto> recentActivities = allProgress.stream()
                .sorted(Comparator.comparing(StudentProgress::getCompletedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(StudentProgressDto::fromEntity)
                .collect(Collectors.toList());

        List<AchievementDto> achievements = getStudentAchievements(studentId);

        // Subject breakdown calculation based on student activity progress
        List<SubjectProgressDto> subjectProgresses = new ArrayList<>();
        for (Subject s : Subject.values()) {
            List<StudentProgress> subjectProgressList = allProgress.stream()
                    .filter(p -> p.getActivity() != null && p.getActivity().getSubject() == s)
                    .collect(Collectors.toList());

            int subjCompLessons = (int) subjectProgressList.stream()
                    .filter(p -> p.isCompleted() && p.getActivity().getActivityType() == ActivityType.LESSON)
                    .count();
            int subjCompQuizzes = (int) subjectProgressList.stream()
                    .filter(p -> p.isCompleted() && p.getActivity().getActivityType() == ActivityType.QUIZ)
                    .count();

            // Default baseline if student has completed activities in subject
            int totalL = Math.max(2, subjCompLessons + 1);
            int totalQ = Math.max(2, subjCompQuizzes + 1);
            int subjPct = (int) Math.min(100, Math.round(((double) (subjCompLessons + subjCompQuizzes) / (totalL + totalQ)) * 100));

            // If overall student has XP/progress, give realistic baseline if subject list is empty
            if (subjectProgressList.isEmpty() && overallPct > 0) {
                subjPct = Math.max(30, overallPct - 15);
            }

            subjectProgresses.add(new SubjectProgressDto(
                    s.name(),
                    subjCompLessons,
                    totalL,
                    subjCompQuizzes,
                    totalQ,
                    subjPct
            ));
        }

        String studentName = student.getUserAccount() != null ? student.getUserAccount().getFullName() : "Student #" + studentId;

        return new StudentOverviewProgressDto(
                studentId,
                studentName,
                totalXp,
                level,
                rank,
                effectiveStreak,
                completedLessons,
                completedQuizzes,
                overallPct,
                subjectProgresses,
                recentActivities,
                achievements
        );
    }
}
