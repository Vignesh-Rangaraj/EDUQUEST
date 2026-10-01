package com.eduquest.repository;

import com.eduquest.domain.LessonContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonContentRepository extends JpaRepository<LessonContent, Long> {

    Optional<LessonContent> findByActivityId(Long activityId);

    @Modifying
    void deleteByActivityId(Long activityId);
}
