package com.ureca.child_recommend.relation.application;


import com.ureca.child_recommend.child.application.ChildService;
import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.infrastructure.ChildRepository;
import com.ureca.child_recommend.child.presentation.dto.ContentsRecommendDto;
import com.ureca.child_recommend.config.redis.util.RedisUtil;
import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import com.ureca.child_recommend.contents.infrastructure.ContentsRepository;
import com.ureca.child_recommend.contents.infrastructure.ContentsVectorRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.relation.domain.FeedBack;
import com.ureca.child_recommend.relation.domain.Enum.FeedBackType;
import com.ureca.child_recommend.relation.infrastructure.FeedBackRepository;
import com.ureca.child_recommend.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FeedBackService {

    private final UserService userService;
    private final ChildService childService;

    private final ChildRepository childRepository;
    private final FeedBackRepository feedBackRepository;
    private final ContentsRepository contentsRepository;
    private final ContentsVectorRepository contentsVectorRepository;

    private final RedisUtil redisUtil;

    public final static String CHILD_LIKED_BOOK_SIMILARITY_RECOMMENDATIONS = "Child_Like_RecommendBook_ChildId : ";
    public static final String REDIS_KEY = "content:likes";

    public List<String> getLikedContents(Long childId) {
        // 좋아요한 피드백 목록을 가져옴
        List<FeedBack> likedFeedbacks = feedBackRepository.findByChild_IdAndType(childId, FeedBackType.LIKE);

        // 좋아요한 컨텐츠의 이름이나 정보만 반환
        return likedFeedbacks.stream()
                .map(feedBack -> feedBack.getContents().getTitle()) // 여기선 컨텐츠의 제목을 반환
                .collect(Collectors.toList());
    }

    public List<Contents> getRecentContents(Long childId) {
            List<Object> recentContentIds = userService.getRecentContents(childId);

        // 컨텐츠 ID로 실제 컨텐츠 정보 조회
        return recentContentIds.stream()
                .map(contentId -> contentsRepository.findById((Long) contentId)
                        .orElseThrow(() -> new IllegalArgumentException("컨텐츠를 찾을 수 없습니다.")))
                .collect(Collectors.toList());
    }


    //좋아요 눌렸을 경우
    @Transactional
    public void likeFeedback(Long userId, Long childId, Long contentsId) {
        Child child = getChild(userId, childId);
        Contents contents = getContents(contentsId);

        FeedBack existingFeedback = feedBackRepository.findByChildIdAndContentsId(childId, contentsId).orElse(null);

        // 기존 피드백 존재 여부 및 상태 처리
        checkAndHandleExistingFeedback(existingFeedback, FeedBackType.DISLIKE, CommonErrorCode.FEEDBACK_ALREADY_LIKED,FeedBackType.LIKE,child,contents,userId);

        // 유사한 컨텐츠 추천 처리
        updateSimilarContentRecommendations(child);
    }
    @Transactional
    public void dislikeFeedback(Long userId, Long childId, Long contentsId) {
        Child child = getChild(userId, childId);
        Contents contents = getContents(contentsId);

        FeedBack existingFeedback = feedBackRepository.findByChildIdAndContentsId(childId, contentsId).orElse(null);

        // 기존 피드백 존재 여부 및 상태 처리
        checkAndHandleExistingFeedback(existingFeedback, FeedBackType.LIKE, CommonErrorCode.FEEDBACK_ALREADY_DISLIKE,FeedBackType.DISLIKE,child,contents,userId);

        // 유사한 컨텐츠 추천 처리
        updateSimilarContentRecommendations(child);
    }



    private long calcMinutesUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        return ChronoUnit.MINUTES.between(now, midnight);
    }

    private Child getChild(Long userId, Long childId) {
        return childRepository.findByIdAndUserId(childId, userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));
    }

    private Contents getContents(Long contentsId) {
        return contentsRepository.findByIdAndStatus(contentsId, ContentsStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CONTENTS_NOT_FOUND));
    }

    private void checkAndHandleExistingFeedback(FeedBack existingFeedback, FeedBackType oppositeType, CommonErrorCode errorCode, FeedBackType currentType, Child child, Contents contents,Long userId) {
        if (existingFeedback != null) {
            // 기존 피드백이 있고, 반대 타입인 경우 예외 발생
            if (existingFeedback.getType().equals(oppositeType)) {
                throw new BusinessException(errorCode);
            }
            // 피드백이 존재하지만 같은 타입일 경우, 삭제 및 Redis 점수 감소
            feedBackRepository.delete(existingFeedback);
            redisUtil.upDownScore(REDIS_KEY, existingFeedback.getContents().getId().toString(), -1);
            //좋아요에 따른 유저 mbti 변화(취소)
            childService.processFeedback(child,userId,contents.getContentsMbti(),oppositeType);
            // 피드백 카운트를 업데이트하여 다음 피드백에 대한 변화량을 감소시킵니다.
            child.downCurrentFeedBackCount();

        } else {
            // 기존 피드백이 없다면 새로운 피드백 저장
            saveNewFeedbackAndIncreaseScore(currentType, child, contents);
            //좋아요에 따른 유저 mbti 변화
            childService.processFeedback(child,userId,contents.getContentsMbti(),currentType);
            // 피드백 카운트를 업데이트하여 다음 피드백에 대한 변화량을 감소시킵니다.
            child.upCurrentFeedBackCount();
        }
    }

    private void saveNewFeedbackAndIncreaseScore(FeedBackType type, Child child, Contents contents) {
        feedBackRepository.save(FeedBack.createFeedBack(type, child, contents));
        redisUtil.upDownScore(REDIS_KEY, contents.getId().toString(), 1);
        redisUtil.expireTodayLikeNum(REDIS_KEY, Duration.ofMinutes(calcMinutesUntilMidnight()));
    }

    private void updateSimilarContentRecommendations(Child child) {
        List<Long> contentsIdList = feedBackRepository.findTop5LikesByChildId(child.getId());
        log.info("Top 5 liked contents: {}", contentsIdList);

        List<Long> similarContentIdList = contentsVectorRepository.findSimilarContentsByAverageEmbedding(contentsIdList);
        log.info("Similar contents: {}", similarContentIdList);

        List<Contents> contentsList = contentsRepository.findByIdIn(similarContentIdList);

        List<ContentsRecommendDto.Response.SimilarBookDto> similarBookDtoList = contentsList.stream()
                .map(o -> ContentsRecommendDto.Response.SimilarBookDto.of(o.getId(), o.getTitle(), o.getPosterUrl()))
                .toList();

        redisUtil.deleteData(CHILD_LIKED_BOOK_SIMILARITY_RECOMMENDATIONS + child.getId());
        redisUtil.saveBooks(CHILD_LIKED_BOOK_SIMILARITY_RECOMMENDATIONS + child.getId(), similarBookDtoList);
    }

}