package com.ureca.child_recommend.child.domain.infrastructure;

import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildMbtiScoreRepository extends JpaRepository<ChildMbtiScore, Long> {
    Optional<ChildMbtiScore> findByChildIdAndStatus(Long id, ChildMbtiScoreStatus status);
}
