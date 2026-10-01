package com.eduquest.repository;

import com.eduquest.domain.StudentMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentMissionRepository extends JpaRepository<StudentMission, Long> {

    List<StudentMission> findByStudentIdAndMissionDate(Long studentId, LocalDate missionDate);

    Optional<StudentMission> findByStudentIdAndMissionKeyAndMissionDate(Long studentId, String missionKey, LocalDate missionDate);

    List<StudentMission> findByStudentIdAndMissionDateAndCompletedFalse(Long studentId, LocalDate missionDate);
}
