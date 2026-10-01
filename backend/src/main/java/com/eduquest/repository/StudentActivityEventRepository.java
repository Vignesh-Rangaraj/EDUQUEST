package com.eduquest.repository;

import com.eduquest.domain.StudentActivityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentActivityEventRepository extends JpaRepository<StudentActivityEvent, Long> {

    List<StudentActivityEvent> findByStudentId(Long studentId);

    List<StudentActivityEvent> findByActivityId(Long activityId);

    @Modifying
    void deleteByActivityId(Long activityId);
}
