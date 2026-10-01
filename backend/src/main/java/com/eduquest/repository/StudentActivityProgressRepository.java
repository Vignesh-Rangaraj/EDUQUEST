package com.eduquest.repository;

import com.eduquest.domain.StudentActivityProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentActivityProgressRepository extends JpaRepository<StudentActivityProgress, Long> {

    List<StudentActivityProgress> findByStudentId(Long studentId);

    List<StudentActivityProgress> findByActivityId(Long activityId);

    Optional<StudentActivityProgress> findByStudentIdAndActivityId(Long studentId, Long activityId);

    long countByStudentIdAndCompletedTrue(Long studentId);

    @Query("SELECT p FROM StudentActivityProgress p WHERE p.studentId IN :studentIds AND p.activityId IN :activityIds AND p.completed = true")
    List<StudentActivityProgress> findCompletedForStudentsAndActivities(
            @Param("studentIds") List<Long> studentIds,
            @Param("activityIds") List<Long> activityIds);

    void deleteByActivityId(Long activityId);
}
