package com.eduquest.repository;

import com.eduquest.domain.StudentQuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentQuizAttemptRepository extends JpaRepository<StudentQuizAttempt, Long> {

    List<StudentQuizAttempt> findByStudentIdAndActivityId(Long studentId, Long activityId);

    List<StudentQuizAttempt> findByStudentId(Long studentId);

    @Modifying
    void deleteByActivityId(Long activityId);
}
