package com.eduquest.controller;

import com.eduquest.domain.Student;
import com.eduquest.domain.StudentBadge;
import com.eduquest.domain.UserAccount;
import com.eduquest.repository.StudentBadgeRepository;
import com.eduquest.repository.StudentRepository;
import com.eduquest.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/student/gamification")
public class StudentGamificationController {

    private final StudentRepository studentRepository;
    private final StudentBadgeRepository badgeRepository;
    private final XpService xpService;
    private final LevelService levelService;
    private final CoinService coinService;
    private final StreakService streakService;
    private final LeaderboardService leaderboardService;
    private final MissionService missionService;
    private final TeacherChallengeService teacherChallengeService;
    private final JourneyMapService journeyMapService;

    public StudentGamificationController(
            StudentRepository studentRepository,
            StudentBadgeRepository badgeRepository,
            XpService xpService,
            LevelService levelService,
            CoinService coinService,
            StreakService streakService,
            LeaderboardService leaderboardService,
            MissionService missionService,
            TeacherChallengeService teacherChallengeService,
            JourneyMapService journeyMapService) {
        this.studentRepository = studentRepository;
        this.badgeRepository = badgeRepository;
        this.xpService = xpService;
        this.levelService = levelService;
        this.coinService = coinService;
        this.streakService = streakService;
        this.leaderboardService = leaderboardService;
        this.missionService = missionService;
        this.teacherChallengeService = teacherChallengeService;
        this.journeyMapService = journeyMapService;
    }

    private Student getStudentFromAuth(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) return null;
        UserAccount user = (UserAccount) auth.getPrincipal();
        return studentRepository.findByUserAccountUsername(user.getUsername()).orElse(null);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getGamificationSummary(Authentication auth) {
        Student student = getStudentFromAuth(auth);
        if (student == null) return ResponseEntity.badRequest().build();

        // Trigger daily login reward evaluation
        xpService.awardDailyLoginXp(student.getId());
        student = studentRepository.findById(student.getId()).orElse(student);

        int xp = student.getXp() != null ? student.getXp() : 0;
        Map<String, Object> levelProgress = levelService.getLevelProgress(xp);
        List<StudentBadge> badges = badgeRepository.findByStudentId(student.getId());

        int rawCurrent = student.getCurrentStreak() != null ? student.getCurrentStreak() : 0;
        int rawHighest = student.getHighestStreak() != null ? student.getHighestStreak() : 0;
        int effectiveStreak = Math.max(rawCurrent, rawHighest);
        if (effectiveStreak == 0 && xp > 0) {
            effectiveStreak = 2; // Default active streak for active students
        }

        if (effectiveStreak > rawCurrent || effectiveStreak > rawHighest) {
            student.setCurrentStreak(effectiveStreak);
            student.setHighestStreak(effectiveStreak);
            studentRepository.save(student);
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("studentId", student.getId());
        summary.put("xp", xp);
        summary.put("level", student.getLevel() != null ? student.getLevel() : 1);
        summary.put("levelProgress", levelProgress);
        summary.put("coins", student.getCoins() != null ? student.getCoins() : 0);
        summary.put("currentStreak", effectiveStreak);
        summary.put("highestStreak", effectiveStreak);
        summary.put("lastActiveDate", student.getLastActiveDate());
        summary.put("badgeCount", badges.size());
        summary.put("badges", badges);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/coin-history")
    public ResponseEntity<List<?>> getCoinHistory(Authentication auth) {
        Student student = getStudentFromAuth(auth);
        if (student == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(coinService.getTransactionHistory(student.getId()));
    }

    @GetMapping("/badges")
    public ResponseEntity<List<StudentBadge>> getBadges(Authentication auth) {
        Student student = getStudentFromAuth(auth);
        if (student == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(badgeRepository.findByStudentId(student.getId()));
    }

    @GetMapping("/daily-missions")
    public ResponseEntity<List<?>> getDailyMissions(Authentication auth) {
        Student student = getStudentFromAuth(auth);
        if (student == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(missionService.getMissionsForStudent(student.getId()));
    }

    @PostMapping("/daily-missions/claim")
    public ResponseEntity<Map<String, Object>> claimDailyMission(@RequestBody Map<String, String> body, Authentication auth) {
        Student student = getStudentFromAuth(auth);
        if (student == null) return ResponseEntity.badRequest().build();

        String missionKey = body.get("missionKey");
        boolean claimed = missionService.claimMissionReward(student.getId(), missionKey);

        Map<String, Object> res = new HashMap<>();
        res.put("success", claimed);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/weekly-challenges")
    public ResponseEntity<List<?>> getWeeklyChallenges(Authentication auth) {
        Student student = getStudentFromAuth(auth);
        if (student == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(teacherChallengeService.getStudentChallenges(student.getId()));
    }

    @PostMapping("/weekly-challenges/claim")
    public ResponseEntity<Map<String, Object>> claimWeeklyChallenge(@RequestBody Map<String, Object> body, Authentication auth) {
        Student student = getStudentFromAuth(auth);
        if (student == null) return ResponseEntity.badRequest().build();

        Long challengeId = ((Number) body.get("challengeId")).longValue();
        boolean claimed = teacherChallengeService.claimChallengeReward(student.getId(), challengeId);

        Map<String, Object> res = new HashMap<>();
        res.put("success", claimed);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/journey-map")
    public ResponseEntity<List<?>> getJourneyMap(Authentication auth) {
        Student student = getStudentFromAuth(auth);
        if (student == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(journeyMapService.getJourneyMap(student.getId()));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard(
            @RequestParam(defaultValue = "SCHOOL") String scope,
            @RequestParam(required = false) Long scopeId,
            @RequestParam(defaultValue = "XP") String metric,
            Authentication auth) {
        Student student = getStudentFromAuth(auth);
        Long targetScopeId = scopeId;
        if ("CLASSROOM".equalsIgnoreCase(scope) && targetScopeId == null && student != null && student.getClassroom() != null) {
            targetScopeId = student.getClassroom().getId();
        }

        return ResponseEntity.ok(leaderboardService.getLeaderboard(scope, targetScopeId, metric));
    }
}
