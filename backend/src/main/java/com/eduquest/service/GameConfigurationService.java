package com.eduquest.service;

import com.eduquest.domain.GameConfiguration;
import com.eduquest.dto.GameConfigurationDto;
import com.eduquest.repository.GameConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GameConfigurationService {

    private final GameConfigurationRepository gameConfigRepository;

    public GameConfigurationService(GameConfigurationRepository gameConfigRepository) {
        this.gameConfigRepository = gameConfigRepository;
    }

    @Transactional(readOnly = true)
    public GameConfigurationDto getGameConfigByActivityId(Long activityId) {
        GameConfiguration gc = gameConfigRepository.findByActivityId(activityId).orElse(null);
        return GameConfigurationDto.fromEntity(gc);
    }

    @Transactional
    public GameConfigurationDto saveGameConfig(Long activityId, String jsonConfig) {
        GameConfiguration gc = gameConfigRepository.findByActivityId(activityId)
                .orElseGet(() -> GameConfiguration.builder().activityId(activityId).build());

        gc.setJsonConfiguration(jsonConfig);
        gc.setUpdatedAt(LocalDateTime.now());

        GameConfiguration saved = gameConfigRepository.save(gc);
        return GameConfigurationDto.fromEntity(saved);
    }
}
