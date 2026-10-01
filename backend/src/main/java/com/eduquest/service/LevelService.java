package com.eduquest.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LevelService {

    public int calculateLevel(int xp) {
        if (xp < 100) return 1;
        if (xp < 250) return 2;
        if (xp < 500) return 3;
        if (xp < 1000) return 4;
        if (xp < 2000) return 5;
        return 5 + ((xp - 1000) / 1000);
    }

    public int getXpThresholdForLevel(int level) {
        if (level <= 1) return 0;
        if (level == 2) return 100;
        if (level == 3) return 250;
        if (level == 4) return 500;
        if (level == 5) return 1000;
        return 1000 + (level - 5) * 1000;
    }

    public Map<String, Object> getLevelProgress(int currentXp) {
        int level = calculateLevel(currentXp);
        int currentLevelThreshold = getXpThresholdForLevel(level);
        int nextLevelThreshold = getXpThresholdForLevel(level + 1);

        int xpInCurrentLevel = currentXp - currentLevelThreshold;
        int xpNeededForNextLevel = nextLevelThreshold - currentLevelThreshold;
        double progressPercent = (double) xpInCurrentLevel / xpNeededForNextLevel * 100.0;
        if (progressPercent > 100.0) progressPercent = 100.0;
        if (progressPercent < 0.0) progressPercent = 0.0;

        Map<String, Object> result = new HashMap<>();
        result.put("level", level);
        result.put("currentXp", currentXp);
        result.put("currentLevelThreshold", currentLevelThreshold);
        result.put("nextLevelThreshold", nextLevelThreshold);
        result.put("xpInCurrentLevel", xpInCurrentLevel);
        result.put("xpNeededForNextLevel", xpNeededForNextLevel);
        result.put("progressPercent", Math.round(progressPercent * 10.0) / 10.0);
        return result;
    }
}
