package com.eduquest.service;

import com.eduquest.domain.Student;
import com.eduquest.domain.StudentCoinTransaction;
import com.eduquest.repository.StudentCoinTransactionRepository;
import com.eduquest.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoinService {

    private final StudentRepository studentRepository;
    private final StudentCoinTransactionRepository coinTransactionRepository;

    public CoinService(StudentRepository studentRepository, StudentCoinTransactionRepository coinTransactionRepository) {
        this.studentRepository = studentRepository;
        this.coinTransactionRepository = coinTransactionRepository;
    }

    @Transactional
    public void awardCoins(Long studentId, int amount, String reason) {
        if (studentId == null || amount <= 0) return;

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        int newCoins = (student.getCoins() != null ? student.getCoins() : 0) + amount;
        student.setCoins(newCoins);
        studentRepository.save(student);

        StudentCoinTransaction tx = StudentCoinTransaction.builder()
                .studentId(studentId)
                .coinsAwarded(amount)
                .reason(reason != null ? reason : "REWARD")
                .createdAt(LocalDateTime.now())
                .build();
        coinTransactionRepository.save(tx);
    }

    public List<StudentCoinTransaction> getTransactionHistory(Long studentId) {
        return coinTransactionRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }
}
