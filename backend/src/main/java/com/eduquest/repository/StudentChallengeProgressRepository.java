package com.eduquest.repository;

import com.eduquest.domain.StudentChallengeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentChallengeProgressRepository extends JpaRepository<StudentChallengeProgress, Long> {

    List<StudentChallengeProgress> findByStudentId(Long studentId);

    Optional<StudentChallengeProgress> findByChallengeIdAndStudentId(Long challengeId, Long studentId);

    List<StudentChallengeProgress> findByChallengeId(Long challengeId);
}
