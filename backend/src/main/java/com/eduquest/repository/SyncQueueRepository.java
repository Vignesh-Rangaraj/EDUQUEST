package com.eduquest.repository;

import com.eduquest.domain.SyncQueue;
import com.eduquest.domain.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SyncQueueRepository extends JpaRepository<SyncQueue, Long> {

    List<SyncQueue> findByStudentId(Long studentId);

    List<SyncQueue> findByStatus(SyncStatus status);

    Optional<SyncQueue> findFirstByClientQueueItemId(String clientQueueItemId);
}
