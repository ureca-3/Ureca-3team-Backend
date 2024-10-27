package com.ureca.child_recommend.child.infrastructure;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.Enum.ChildStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child,Long> {
    List<Child> findByUserIdAndStatus(Long userId, ChildStatus status);

    Optional<Child> findByIdAndUserId(Long childId, Long UserId);

}
