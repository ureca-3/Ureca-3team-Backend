package com.ureca.child_recommend.viewing.infrastructure;

import com.ureca.child_recommend.relation.domain.Enum.FeedBackType;
import com.ureca.child_recommend.relation.domain.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingFeedBackRepository extends JpaRepository<FeedBack, Long> {


    // childId와 contentId로 FeedBack 기록을 삭제
    void deleteByChildIdAndContentsId(Long childId, Long contentsId);

    // 특정 자녀가 특정 콘텐츠에 대해 좋아요를 눌렀는지 여부 확인
    boolean existsByChildIdAndContentsIdAndType(Long childId, Long contentsId, FeedBackType type);
}
