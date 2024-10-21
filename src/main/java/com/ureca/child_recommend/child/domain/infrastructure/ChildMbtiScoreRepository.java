package com.ureca.child_recommend.child.domain.infrastructure;

import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChildMbtiScoreRepository extends JpaRepository<ChildMbtiScore, Long> {
    Optional<ChildMbtiScore> findByChildIdAndStatus(Long id, ChildMbtiScoreStatus status);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM child_mbti_score cms WHERE cms.update_at <= :thresholdDateTime AND cms.status = :childMbtiScoreStatus", nativeQuery = true)
    void deleteByUpdateAtAndStatus(LocalDateTime thresholdDateTime, ChildMbtiScoreStatus childMbtiScoreStatus);
}
