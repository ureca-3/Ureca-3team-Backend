package com.ureca.child_recommend.child.infrastructure;

import com.ureca.child_recommend.child.domain.ChildMbti;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildMbtiRepository extends JpaRepository<ChildMbti,Long> {
    Optional<ChildMbti> findByChildIdAndStatus(Long id, ChildMbtiStatus status);
}
