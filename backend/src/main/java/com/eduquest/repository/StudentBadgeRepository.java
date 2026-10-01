package com.eduquest.repository;

import com.eduquest.domain.StudentBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentBadgeRepository extends JpaRepository<StudentBadge, Long> {

    List<StudentBadge> findByStudentId(Long studentId);

    Optional<StudentBadge> findByStudentIdAndBadgeCode(Long studentId, String badgeCode);
}
