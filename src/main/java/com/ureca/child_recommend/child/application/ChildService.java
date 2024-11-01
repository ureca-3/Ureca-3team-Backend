package com.ureca.child_recommend.child.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.ChildVector;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import com.ureca.child_recommend.child.domain.Enum.ChildStatus;
import com.ureca.child_recommend.child.infrastructure.ChildMbtiScoreRepository;
import com.ureca.child_recommend.child.infrastructure.ChildRepository;
import com.ureca.child_recommend.child.infrastructure.ChildVectorRepository;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import com.ureca.child_recommend.child.presentation.dto.ContentsRecommendDto;
import com.ureca.child_recommend.config.embedding.EmbeddingUtil;
import com.ureca.child_recommend.global.application.S3Service;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.relation.domain.FeedBack;
import com.ureca.child_recommend.relation.infrastructure.FeedBackRepository;
import com.ureca.child_recommend.user.domain.Users;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMbtiScoreRepository childMbtiScoreRepository;
    private final UserRepository userRepository;
    private final ChildVectorRepository childVectorRepository;
    private final EmbeddingUtil embeddingUtil;
    private final FeedBackRepository feedBackRepository;
    private final S3Service s3Service;

    public ChildDto.Response findChildById(Long childId) {
        // childId로 Child를 찾아서 없으면 예외 던지기
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        // Child를 ChildDto.Response로 변환하여 반환
        return ChildDto.Response.fromEntity(child);
    }

    @Transactional
    public Long createChildProfile(Long userId, ChildDto.Request childRequest, MultipartFile image) {
        // userId로 사용자 정보 가져오기
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));

        String profileUrl = s3Service.uploadFileImage(image, "childProfile");
        // 도메인의 createChildProfile 메서드 호출
        Child child = Child.createChildProfile(
                childRequest.getName(),
                childRequest.getGender(),
                childRequest.getBirthday(),
                profileUrl,
                childRequest.getAge(),
                user
        );

        // 자녀 프로필 저장
        childRepository.save(child);

        return child.getId();
    }

    // 자녀 조회
    public List<ChildDto.Response> getAllChildren(Long userId) {
        List<Child> children = childRepository.findByUserIdAndStatus(userId, ChildStatus.ACTIVE); // 조건에 맞는 조회
        return children.stream()
                .map(ChildDto.Response::fromEntity) // fromEntity 메서드로 변환
                .collect(Collectors.toList());
    }



    // 자녀 상세 조회
    public Child getChildById(Long userId, Long childId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));
        if(user != child.getUser()) {
            throw new BusinessException(CommonErrorCode.CHILD_NOT_FOUND);
        }
        return child;
    }


    // 자녀 수정
    @Transactional
    public ChildDto.Response updateChild(Long childId, ChildDto.Request childRequest) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        // 요청 DTO에서 정보를 가져와 업데이트
       child.updateChildInfo(childRequest.getName(),
                childRequest.getGender(), childRequest.getBirthday(), childRequest.getProfileUrl(), childRequest.getAge());

        return ChildDto.Response.fromEntity(child); // 업데이트된 정보를 Response DTO로 변환하여 반환
    }


    // 자녀 삭제
    @Transactional
    public void deleteChild(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));
        Child.updateChildStatus(child);
    }

    // 자녀 사진 변경
    public void updateChildProfile(Long childId, MultipartFile newImage) throws IOException {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));
        String profileUrl = s3Service.updateFileImage(child.getProfileUrl(), newImage);
        child.setProfileUrl(profileUrl);
    }

    // 자녀 MBTI SCORE 조회
    public ChildMbtiScore getChildMbti(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        // ChildMbtiScore를 조회
        ChildMbtiScore childMbtiScore = childMbtiScoreRepository.findByChildIdAndStatus(childId, ChildMbtiScoreStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.CHILDMBTI_NOT_FOUND));

        return childMbtiScore; // 또는 필요한 값을 반환
    }


    /**
     * 특정 자녀의 id를 통해 - (나이,성별,mbti)로 임베딩 벡터 생성
     * @param childId : 자녀 pk
     */
    @Transactional
    public void inputEmbedding(Long userId,Long childId) {


        Child child = childRepository.findByIdAndUserId(childId,userId).orElseThrow(()-> new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        ChildMbtiScore childMbtiScore = childMbtiScoreRepository.findByChildIdAndStatus(child.getId(), ChildMbtiScoreStatus.ACTIVE).orElseThrow(()-> new BusinessException(CommonErrorCode.ASSESSMENT_NOT_FOUND));

        String input = String.format("나이: %d, 성별: %s, MBTI: %s-%s, %s-%s, %s-%s, %s-%s",
                child.getAge(),
                child.getGender(),
                childMbtiScore.getSnType(), childMbtiScore.getEiScore()+"%",
                childMbtiScore.getSnType(), childMbtiScore.getSnScore()+"%",
                childMbtiScore.getTfType(), childMbtiScore.getTfScore()+"%",
                childMbtiScore.getJpType(), childMbtiScore.getJpScore()+"%");

        //임베딩 벡터 생성
        float[] childEmbedding = embeddingUtil.createEmbedding(input);

        saveChildEmbedding(childEmbedding,child);
    }

    /**
     * 자녀의 임베딩 벡터값 디비에 저장
     * @param childEmbedding : 자녀의 임베딩 벡터 값
     * @param child : 자녀 객체
     */
    protected void saveChildEmbedding(float[] childEmbedding, Child child){

        ChildVector childVector = ChildVector.createChildVector(childEmbedding,child);
        childVectorRepository.save(childVector);

    }

    /**
     * 해당 자녀와 유사한 유저 목록을 통해 유저들이 좋아요 누른 도서를 추천 받음.
     * @param userId : 부모(유저) pk
     * @param childId : 자녀 pk
     * @return SimilarBookDto : 변환값
     */
    public List<ContentsRecommendDto.Response.SimilarBookDto> getSimilarUsersBooks(Long userId, Long childId) {
        //해당 자녀가 해당 부모의 자녀인지 확인
        childRepository.findByIdAndUserId(childId,userId).orElseThrow(()->new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        //해당 자녀와 비슷한 유저 목록 조회
        List<Long> results = childVectorRepository.findSimilarChildId(childId);

        // 비슷한 유저들의 피드백 조회(엔티티 그래프를 통해 N+1 발생을 없앰 +IN을 통해서 리스트에 있는 목록을 한번에 다 가져옴)
        List<FeedBack> feedBackList = feedBackRepository.findAllByChildIdIn(results);

        // 피드백 목록을 SimilarBookDto로 변환
        return feedBackList.stream()
                .map(o-> ContentsRecommendDto.Response.SimilarBookDto.of(o.getContents().getId(),o.getContents().getTitle(),o.getContents().getPosterUrl()))
                .collect(Collectors.toList());

    }

    /**
     * 해당 자녀와 비슷한 유저의 리스트와 유사도 출력
     * @param userId : 부모(유저) pk
     * @param childId : 자녀 pk
     * @return SimilarUserDto : 반환값
     */
    public List<ContentsRecommendDto.Response.SimilarUserDto> getSimilarTotalUsers(Long userId, Long childId) {
        //해당 자녀가 해당 부모의 자녀인지 확인
        childRepository.findByIdAndUserId(childId,userId).orElseThrow(()->new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        //해당 자녀와 비슷한 유저 목록 조회
        List<Object[]> results = childVectorRepository.findSimilarVectorsByChildId(childId);

        // Object[] 배열에서 DTO로 변환
        return results.stream()
                .map(result -> new ContentsRecommendDto.Response.SimilarUserDto(
                        ((Number) result[0]).longValue(),   // child_id
                        ((Number) result[1]).floatValue()   // similarity
                ))
                .collect(Collectors.toList());
    }

}
