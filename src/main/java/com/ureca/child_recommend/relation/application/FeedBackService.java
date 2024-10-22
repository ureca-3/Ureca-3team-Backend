package com.ureca.child_recommend.relation.application;


import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.relation.FeedBack;
import com.ureca.child_recommend.relation.Enum.FeedBackType;
import com.ureca.child_recommend.relation.infrastructure.FeedBackRepository;
import com.ureca.child_recommend.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedBackService {

    private final FeedBackRepository feedBackRepository;
    private final ContentsRepository contentsRepository;
    private final UserService userService;


    public List<String> getLikedContents(Long childId) {
        // 좋아요한 피드백 목록을 가져옴
        List<FeedBack> likedFeedbacks = feedBackRepository.findByChild_IdAndType(childId, FeedBackType.LIKE);

        // 좋아요한 컨텐츠의 이름이나 정보만 반환
        return likedFeedbacks.stream()
                .map(feedBack -> feedBack.getContents().getTitle()) // 여기선 컨텐츠의 제목을 반환
                .collect(Collectors.toList());
    }

    public List<Contents> getRecentContents(Long userId) {
        List<Object> recentContentIds = userService.getRecentContents(userId);

        // 컨텐츠 ID로 실제 컨텐츠 정보 조회
        return recentContentIds.stream()
                .map(contentId -> contentsRepository.findById((Long) contentId)
                        .orElseThrow(() -> new IllegalArgumentException("컨텐츠를 찾을 수 없습니다.")))
                .collect(Collectors.toList());
    }
}