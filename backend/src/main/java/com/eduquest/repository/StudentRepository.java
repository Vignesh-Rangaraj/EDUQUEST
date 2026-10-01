package com.eduquest.repository;

import com.eduquest.domain.Student;
import com.eduquest.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserAccount(UserAccount userAccount);
    Optional<Student> findByUserAccountUsername(String username);
    List<Student> findByClassroom_Id(Long classroomId);
    List<Student> findByClassroomId(Long classroomId);
    List<Student> findByParent_Id(Long parentId);
}
