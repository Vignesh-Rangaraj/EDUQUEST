package com.eduquest.repository;

import com.eduquest.domain.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByActivityIdOrderByDisplayOrderAsc(Long activityId);

    long countByActivityId(Long activityId);

    @Modifying
    void deleteByActivityId(Long activityId);
}
