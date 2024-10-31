package com.ureca.child_recommend.relation.infrastructure;


import com.ureca.child_recommend.relation.FeedBack;
import com.ureca.child_recommend.relation.Enum.FeedBackType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {
    // 특정 자녀가 좋아요한 컨텐츠 조회
    List<FeedBack> findByChild_IdAndType(Long childId, FeedBackType type);

    @EntityGraph(attributePaths = {"contents"})
    List<FeedBack> findAllByChildIdIn(List<Long> childIds);

    @Query("SELECT c.id FROM FeedBack f " +
            "INNER JOIN f.contents c " +
            "WHERE f.child.id = :childId AND f.type = 'LIKE' " +
            "ORDER BY f.createAt DESC")
    List<Long> findTop5LikesByChildId(@Param("childId") Long childId);


}

