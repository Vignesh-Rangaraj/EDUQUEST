package com.eduquest.service;

import com.eduquest.domain.StudentMission;
import com.eduquest.repository.StudentActivityProgressRepository;
import com.eduquest.repository.StudentMissionRepository;
import com.eduquest.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MissionService {

    private final StudentMissionRepository missionRepository;
    private final StudentActivityProgressRepository progressRepository;
    private final StudentRepository studentRepository;
    private final XpService xpService;
    private final CoinService coinService;

    public MissionService(
            StudentMissionRepository missionRepository,
            StudentActivityProgressRepository progressRepository,
            StudentRepository studentRepository,
            XpService xpService,
            CoinService coinService) {
        this.missionRepository = missionRepository;
        this.progressRepository = progressRepository;
        this.studentRepository = studentRepository;
        this.xpService = xpService;
        this.coinService = coinService;
    }

    @Transactional
    public List<StudentMission> getMissionsForStudent(Long studentId) {
        LocalDate today = LocalDate.now();
        List<StudentMission> existing = missionRepository.findByStudentIdAndMissionDate(studentId, today);

        if (existing.isEmpty()) {
            // Generate 3 default daily missions for today
            StudentMission m1 = StudentMission.builder()
                    .studentId(studentId)
                    .missionKey("LESSON_READER")
                    .title("Read 1 Lesson")
                    .description("Open and read any lesson content")
                    .targetCount(1)
                    .currentProgress(0)
                    .xpReward(20)
                    .coinReward(5)
                    .missionDate(today)
                    .build();

            StudentMission m2 = StudentMission.builder()
                    .studentId(studentId)
                    .missionKey("QUIZ_STAR")
                    .title("Complete 1 Quiz")
                    .description("Answer all questions in a practice quiz")
                    .targetCount(1)
                    .currentProgress(0)
                    .xpReward(30)
                    .coinReward(10)
                    .missionDate(today)
                    .build();

            StudentMission m3 = StudentMission.builder()
                    .studentId(studentId)
                    .missionKey("XP_HUNTER")
                    .title("Earn 50 XP")
                    .description("Gain 50 XP from learning activities today")
                    .targetCount(50)
                    .currentProgress(0)
                    .xpReward(50)
                    .coinReward(15)
                    .missionDate(today)
                    .build();

            missionRepository.save(m1);
            missionRepository.save(m2);
            missionRepository.save(m3);
            existing = missionRepository.findByStudentIdAndMissionDate(studentId, today);
        }

        // Update progress dynamically
        long lessonsToday = progressRepository.countByStudentIdAndCompletedTrue(studentId);
        for (StudentMission m : existing) {
            if (!m.getCompleted()) {
                if ("LESSON_READER".equals(m.getMissionKey())) {
                    int prog = (int) Math.min(lessonsToday, m.getTargetCount());
                    m.setCurrentProgress(prog);
                    if (prog >= m.getTargetCount()) m.setCompleted(true);
                } else if ("QUIZ_STAR".equals(m.getMissionKey())) {
                    int prog = (int) Math.min(lessonsToday, m.getTargetCount());
                    m.setCurrentProgress(prog);
                    if (prog >= m.getTargetCount()) m.setCompleted(true);
                } else if ("XP_HUNTER".equals(m.getMissionKey())) {
                    int xp = studentRepository.findById(studentId).map(s -> s.getXp() != null ? s.getXp() : 0).orElse(0);
                    int prog = Math.min(xp, m.getTargetCount());
                    m.setCurrentProgress(prog);
                    if (prog >= m.getTargetCount()) m.setCompleted(true);
                }
                missionRepository.save(m);
            }
        }

        return existing;
    }

    @Transactional
    public boolean claimMissionReward(Long studentId, String missionKey) {
        LocalDate today = LocalDate.now();
        StudentMission mission = missionRepository.findByStudentIdAndMissionKeyAndMissionDate(studentId, missionKey, today)
                .orElse(null);

        if (mission == null || !mission.getCompleted() || mission.getClaimed()) {
            return false;
        }

        mission.setClaimed(true);
        missionRepository.save(mission);

        xpService.awardXp(studentId, null, mission.getXpReward(), "DAILY_MISSION_REWARD_" + missionKey);
        coinService.awardCoins(studentId, mission.getCoinReward(), "DAILY_MISSION_REWARD_" + missionKey);

        return true;
    }
}
