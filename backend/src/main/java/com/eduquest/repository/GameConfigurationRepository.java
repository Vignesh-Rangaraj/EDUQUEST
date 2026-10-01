package com.eduquest.repository;

import com.eduquest.domain.GameConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameConfigurationRepository extends JpaRepository<GameConfiguration, Long> {

    Optional<GameConfiguration> findByActivityId(Long activityId);

    @Modifying
    void deleteByActivityId(Long activityId);
}
