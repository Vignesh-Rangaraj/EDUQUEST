package com.eduquest.repository;

import com.eduquest.domain.StudentModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentModuleProgressRepository extends JpaRepository<StudentModuleProgress, Long> {

    List<StudentModuleProgress> findByStudentId(Long studentId);

    Optional<StudentModuleProgress> findByStudentIdAndModuleId(Long studentId, Long moduleId);

    long countByStudentIdAndCompletedTrue(Long studentId);
}
