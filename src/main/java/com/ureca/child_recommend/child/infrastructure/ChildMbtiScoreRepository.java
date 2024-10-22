package com.ureca.child_recommend.child.infrastructure;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildMbtiScoreRepository extends JpaRepository<ChildMbtiScore,Long> {

    Optional<ChildMbtiScore> findByChild(Child child);
}
