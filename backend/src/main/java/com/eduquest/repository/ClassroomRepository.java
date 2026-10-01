package com.eduquest.repository;

import com.eduquest.domain.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByName(String name);
    Optional<Classroom> findByGradeAndSection(Integer grade, String section);
}
