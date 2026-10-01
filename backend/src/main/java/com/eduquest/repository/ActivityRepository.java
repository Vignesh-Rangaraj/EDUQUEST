package com.eduquest.repository;

import com.eduquest.domain.Activity;
import com.eduquest.domain.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByCreatedByTeacherId(Long createdByTeacherId);

    List<Activity> findByStatus(ActivityStatus status);

    List<Activity> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);

    List<Activity> findByModuleIdAndStatusOrderByDisplayOrderAsc(Long moduleId, ActivityStatus status);

    @Query("SELECT a FROM Activity a WHERE a.status = :status AND (a.assignedClassroomId IS NULL OR a.assignedClassroomId = :classroomId)")
    List<Activity> findPublishedForClassroom(@Param("classroomId") Long classroomId, @Param("status") ActivityStatus status);

    List<Activity> findByAssignedClassroomId(Long assignedClassroomId);

    List<Activity> findByPrerequisiteActivityId(Long prerequisiteActivityId);
}
