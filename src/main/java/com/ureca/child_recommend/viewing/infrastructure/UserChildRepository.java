package com.ureca.child_recommend.viewing.infrastructure;

import com.ureca.child_recommend.child.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserChildRepository extends JpaRepository<Child, Long> {

    Optional<Child> findByIdAndUserId(Long id, Long userId);
}