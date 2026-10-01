package com.eduquest.repository;

import com.eduquest.domain.StudentCoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCoinTransactionRepository extends JpaRepository<StudentCoinTransaction, Long> {
    List<StudentCoinTransaction> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
