package com.ureca.child_recommend.child.infrastructure;

import com.ureca.child_recommend.child.domain.ChildMbti;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiStatus;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChildMbtiRepository extends JpaRepository<ChildMbti,Long> {
    Optional<ChildMbti> findByChildIdAndStatus(Long id, ChildMbtiStatus status);

    Optional<ChildMbti> findByChildMbtiScore(ChildMbtiScore childMbtiScore);

    @Query("SELECT c.mbtiResult FROM ChildMbti c WHERE c.childMbtiScore = :childMbtiScore AND c.status IN ('ACTIVE', 'NONACTIVE')")
    String findMbtiResultByChildMbtiScore(@Param("childMbtiScore") ChildMbtiScore childMbtiScore);


}
