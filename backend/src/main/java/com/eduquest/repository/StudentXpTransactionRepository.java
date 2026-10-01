package com.eduquest.repository;

import com.eduquest.domain.StudentXpTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentXpTransactionRepository extends JpaRepository<StudentXpTransaction, Long> {

    List<StudentXpTransaction> findByStudentId(Long studentId);

    Optional<StudentXpTransaction> findByStudentIdAndActivityId(Long studentId, Long activityId);

    @Modifying
    void deleteByActivityId(Long activityId);
}
