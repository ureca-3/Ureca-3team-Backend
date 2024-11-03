package com.ureca.child_recommend.child.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbti;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.ChildVector;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiStatus;
import com.ureca.child_recommend.child.infrastructure.ChildMbtiRepository;
import com.ureca.child_recommend.child.infrastructure.ChildMbtiScoreRepository;
import com.ureca.child_recommend.child.infrastructure.ChildRepository;
import com.ureca.child_recommend.child.infrastructure.ChildVectorRepository;
import com.ureca.child_recommend.child.presentation.dto.MbtiDto;
import com.ureca.child_recommend.config.embedding.EmbeddingUtil;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MbtiService {

    private final ChildService childService;

    private final ChildMbtiRepository childMbtiRepository;
    private final ChildMbtiScoreRepository childMbtiScoreRepository;
    private final ChildRepository childRepository;
    private final EmbeddingUtil embeddingUtil;
    private final ChildVectorRepository childVectorRepository;

    @Transactional
    public MbtiDto.Response.assessmentMbtiDto saveMbtiResult(Long userId, MbtiDto.Request.assessmentMbtiDto dto, Long child_id) {

        String result = calculateMbti(dto);
        Child child = childService.getChildById(userId, child_id);
        child.resetCurrentFeedBackCount(); //피드백 개수 리셋

        // 1. 자녀 PK로 ChildMbti 데이터 조회
        Optional<ChildMbti> existingChildMbti = childMbtiRepository.findByChildIdAndStatus(child.getId(), ChildMbtiStatus.ACTIVE);

        // 2. 기존 데이터가 있으면 status 값을 NONACTIVE로 변경
        existingChildMbti.ifPresent(childMbti -> {
            childMbti.updateStatus(ChildMbtiStatus.NONACTIVE);
        });

        // 1. 자녀 PK로 ChildMbti 데이터 조회
        Optional<ChildMbtiScore> existingChildMbtiScore = childMbtiScoreRepository.findByChildIdAndStatus(child.getId(), ChildMbtiScoreStatus.ACTIVE);

        // 2. 기존 데이터가 있으면 status 값을 NONACTIVE로 변경
        existingChildMbtiScore.ifPresent(ChildMbtiScore -> {
            ChildMbtiScore.updateStatus(ChildMbtiScoreStatus.NONACTIVE);
        });

        ChildMbtiScore newChildMbtiScore = childMbtiScoreRepository.save(ChildMbtiScore.enrollToMbtiScore(dto.getM(),dto.getB(), dto.getT(), dto.getI(), child));

        // ChildMbti 저장
        childMbtiRepository.save(ChildMbti.enrollToMbti(result, child,newChildMbtiScore));

        // ChildVector 삭제 후 즉시 반영
        childVectorRepository.findByChild(child).ifPresent(childVector -> {
            childVectorRepository.delete(childVector);
            childVectorRepository.flush();
        });
        inputEmbedding(child,newChildMbtiScore);

        return MbtiDto.Response.assessmentMbtiDto.of(result);
    }

    @Transactional
    public void deleteMbti(Long childMbtiScoreId) {
        ChildMbtiScore deleteChildMbti = childMbtiScoreRepository.findById(childMbtiScoreId).orElseThrow(()->new BusinessException(CommonErrorCode.ASSESSMENT_NOT_FOUND));
        deleteChildMbti.updateStatus(ChildMbtiScoreStatus.DELETE);

        ChildMbti childMbti = childMbtiRepository.findByChildMbtiScore(deleteChildMbti).orElseThrow(()->new BusinessException(CommonErrorCode.ASSESSMENT_NOT_FOUND));

        childMbti.updateStatus(ChildMbtiStatus.DELETE);

    }

    public String calculateMbti(MbtiDto.Request.assessmentMbtiDto dto){
        String e_i;
        String s_n;
        String t_f;
        String j_p;

        if(dto.getM() > 50) e_i = "E"; else e_i = "I";
        if(dto.getB() > 50) s_n = "S"; else s_n = "N";
        if(dto.getT() > 50) t_f = "T"; else t_f = "F";
        if(dto.getI() > 50) j_p = "J"; else j_p = "P";

        String result = new StringBuilder()
                .append(e_i)
                .append(s_n)
                .append(t_f)
                .append(j_p)
                .toString();

        return result;
    }

    public List<MbtiDto.Response.assessmentMbtiResultDto> getAssessmentMbtiResults(Long userId, Long childId) {

        childRepository.findByIdAndUserId(childId,userId).orElseThrow(()->new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));

        //mbti 별 스코어 가져오기
        List<ChildMbtiScore> childMbtiScoreList = childMbtiScoreRepository.
                findByChildIdAndStatusIn(childId, Arrays.asList(ChildMbtiScoreStatus.ACTIVE,ChildMbtiScoreStatus.NONACTIVE));

        return childMbtiScoreList.stream()
                .map(o -> {
                    String mbtiResult = childMbtiRepository.findMbtiResultByChildMbtiScore(o);
                    return MbtiDto.Response.assessmentMbtiResultDto.of(o,mbtiResult);
                })
                .collect(Collectors.toList());
    }

    /**
     *  (나이,성별,mbti)로 임베딩 벡터 생성
     */
    @Transactional
    public void inputEmbedding(Child child,ChildMbtiScore childMbtiScore) {

        String input = String.format("나이: %d, 성별: %s, MBTI: %s-%s, %s-%s, %s-%s, %s-%s",
                child.getAge(),
                child.getGender(),
                childMbtiScore.getEiType(), childMbtiScore.getEiScore()+"%",
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

}
