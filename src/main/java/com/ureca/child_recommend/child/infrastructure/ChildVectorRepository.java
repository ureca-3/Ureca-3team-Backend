package com.ureca.child_recommend.child.infrastructure;

import com.ureca.child_recommend.child.domain.ChildVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChildVectorRepository extends JpaRepository<ChildVector,Long> {
    @Query(nativeQuery = true,
            value = "SELECT cv2.child_id, 1 - (cv2.embedding <=> cv1.embedding) AS similarity " +
                    "FROM child_vector cv1, child_vector cv2 " +
                    "WHERE cv1.child_id = ?1 " +
                    "AND cv1.child_id != cv2.child_id " +
                    "ORDER BY cv2.embedding <-> cv1.embedding " +
                    "LIMIT 20")
    List<Object[]> findSimilarVectorsByChildId(Long childId);

    @Query(nativeQuery = true,
            value = "SELECT cv2.child_id " +
                    "FROM child_vector cv1, child_vector cv2 " +
                    "WHERE cv1.child_id = ?1 " +
                    "AND cv1.child_id != cv2.child_id " +
                    "ORDER BY cv2.embedding <-> cv1.embedding " +
                    "LIMIT 15")
    List<Long> findSimilarChildId(Long childId);
}


