package com.eduquest.repository;

import com.eduquest.domain.StudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {

    List<StudentProgress> findByStudentId(Long studentId);

    Optional<StudentProgress> findByStudentIdAndActivityId(Long studentId, Long activityId);

    @Query("SELECT p FROM StudentProgress p WHERE p.student.id IN :studentIds AND p.activity.id IN :activityIds AND p.completed = true")
    List<StudentProgress> findCompletedForStudentsAndActivities(
            @Param("studentIds") List<Long> studentIds,
            @Param("activityIds") List<Long> activityIds);

    @Modifying
    @Query("DELETE FROM StudentProgress p WHERE p.activity.id = :activityId")
    void deleteByActivityId(@Param("activityId") Long activityId);
}
