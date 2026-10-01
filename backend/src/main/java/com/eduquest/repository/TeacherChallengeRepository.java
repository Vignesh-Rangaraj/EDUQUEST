package com.eduquest.repository;

import com.eduquest.domain.TeacherChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TeacherChallengeRepository extends JpaRepository<TeacherChallenge, Long> {

    List<TeacherChallenge> findByClassroomId(Long classroomId);

    List<TeacherChallenge> findByClassroomIdAndArchivedFalse(Long classroomId);

    List<TeacherChallenge> findByClassroomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long classroomId, LocalDate now1, LocalDate now2);

    List<TeacherChallenge> findByClassroomIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndArchivedFalse(Long classroomId, LocalDate now1, LocalDate now2);

    List<TeacherChallenge> findByTeacherId(Long teacherId);

    long countByArchivedFalse();

    long countByArchivedTrue();
}
