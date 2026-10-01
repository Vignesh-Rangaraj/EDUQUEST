package com.eduquest.repository;

import com.eduquest.domain.Teacher;
import com.eduquest.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserAccount(UserAccount userAccount);
    Optional<Teacher> findByUserAccountUsername(String username);
    Optional<Teacher> findByClassroom_Id(Long classroomId);
}
