package com.eduquest.repository;

import com.eduquest.domain.Parent;
import com.eduquest.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByUserAccount(UserAccount userAccount);
    Optional<Parent> findByUserAccountUsername(String username);
}
