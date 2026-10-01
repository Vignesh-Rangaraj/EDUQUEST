package com.eduquest.repository;

import com.eduquest.domain.DailyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {
    Optional<DailyMission> findByMissionKey(String missionKey);
}
