package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JourneyMapService {

    private final StudentRepository studentRepository;

    public JourneyMapService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Map<String, Object>> getJourneyMap(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        int currentXp = student != null && student.getXp() != null ? student.getXp() : 0;

        List<Map<String, Object>> stages = new ArrayList<>();

        stages.add(createStage(1, "Village", "🏡", 0, 100, currentXp));
        stages.add(createStage(2, "Farm", "🌾", 100, 250, currentXp));
        stages.add(createStage(3, "Forest", "🌲", 250, 500, currentXp));
        stages.add(createStage(4, "River", "🌊", 500, 1000, currentXp));
        stages.add(createStage(5, "Mountain", "⛰️", 1000, 2000, currentXp));
        stages.add(createStage(6, "Castle", "🏰", 2000, 5000, currentXp));

        return stages;
    }

    private Map<String, Object> createStage(int stageIndex, String name, String icon, int minXp, int maxXp, int currentXp) {
        Map<String, Object> stage = new HashMap<>();
        stage.put("stageIndex", stageIndex);
        stage.put("name", name);
        stage.put("icon", icon);
        stage.put("minXp", minXp);
        stage.put("maxXp", maxXp);
        stage.put("isUnlocked", currentXp >= minXp);

        boolean isCurrent = currentXp >= minXp && (currentXp < maxXp || stageIndex == 6);
        stage.put("isCurrent", isCurrent);

        double percent = 0.0;
        if (currentXp >= maxXp) {
            percent = 100.0;
        } else if (currentXp <= minXp) {
            percent = 0.0;
        } else {
            percent = (double) (currentXp - minXp) / (maxXp - minXp) * 100.0;
        }
        stage.put("progressPercent", Math.round(percent * 10.0) / 10.0);

        return stage;
    }
}
