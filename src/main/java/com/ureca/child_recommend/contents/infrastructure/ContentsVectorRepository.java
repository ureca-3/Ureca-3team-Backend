package com.ureca.child_recommend.contents.infrastructure;

import com.ureca.child_recommend.contents.domain.ContentsVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ContentsVectorRepository extends JpaRepository<ContentsVector,Long> {

    @Query(nativeQuery = true,
            value = "SELECT cv2.contents_id " +
                    "FROM contents_vector cv2 " +
                    "WHERE cv2.contents_id NOT IN ( " +
                    "SELECT f.contents_id " +
                    "FROM feed_back f " +
                    "WHERE f.type = 'DISLIKE' ) AND " +
                    "cv2.contents_id NOT IN (:contentIds) " +
                    "ORDER BY cv2.embedding <-> " +
                    "(SELECT AVG(cv.embedding) " +
                    "FROM contents_vector cv " +
                    "WHERE cv.contents_id IN (:contentIds)) " +
                    "LIMIT 15")
    List<Long> findSimilarContentsByAverageEmbedding(@Param("contentIds") List<Long> contentIds);

}
