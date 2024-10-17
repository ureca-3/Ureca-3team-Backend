package com.ureca.child_recommend.child.domain.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbti;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiStatus;
import com.ureca.child_recommend.child.domain.infrastructure.ChildMbtiRepository;
import com.ureca.child_recommend.child.domain.infrastructure.ChildMbtiScoreRepository;
import com.ureca.child_recommend.child.domain.presentation.dto.MbtiDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MbtiService {

    @Autowired
    ChildMbtiRepository childMbtiRepository;
    @Autowired
    ChildMbtiScoreRepository childMbtiScoreRepository;

    public String calculateMbti(int score, String type1, String type2){
        return score <= 50 ? type1 : type2;
    }

    // dto 에서 받아온 스코어 값으로 MBTI 계산
    public void setMbti(MbtiDto dto) {
        dto.setMbti1(calculateMbti(dto.getM(), "I", "E"));
        dto.setMbti2(calculateMbti(dto.getB(), "N", "S"));
        dto.setMbti3(calculateMbti(dto.getT(), "F", "T"));
        dto.setMbti4(calculateMbti(dto.getI(), "P", "J"));

        String result = dto.getMbti1() + dto.getMbti2() + dto.getMbti3() + dto.getMbti4();
        dto.setResult(result);
    }

    // DB에 저장
    public void saveMbtiResult(MbtiDto dto, Child child) {
        setMbti(dto);
        /**
         * child_mbti 테이블에 데이터 조회
         * 1. 데이터 있으면
         *      자녀pk 를 가지고 기존 데이터들의 status 값을 모두 NONACTIVE 값으로 변경 후 insert
         * 2. 데이터 없으면
         *      childMbti insert
         * */
        // 1. 자녀 PK로 ChildMbti 데이터 조회
        Optional<ChildMbti> existingChildMbti = childMbtiRepository.findByChildIdAndStatus(child.getId(), ChildMbtiStatus.ACTIVE);

        // 기존 데이터가 있으면 status 값을 NONACTIVE로 변경
        existingChildMbti.ifPresent(childMbti -> {
            childMbti.updateStatus(ChildMbtiStatus.NONACTIVE);
        });

        // ChildMbti 저장
        ChildMbti childMbti = ChildMbti.builder()
                .mbtiResult(dto.getResult())
                .status(ChildMbtiStatus.ACTIVE)
                .child(child)
                .build();

        childMbtiRepository.save(childMbti);

        /**
         * child_mbti_score 테이블에 데이터 조회
         * 1. 데이터 있으면
         *      자녀pk 를 가지고 기존 데이터들의 status 값을 모두 NONACTIVE 값으로 변경 후 insert
         * 2. 데이터 없으면
         *      child_mbti_score insert
         * */

        // 1. 자녀 PK로 ChildMbti 데이터 조회
        Optional<ChildMbtiScore> existingChildMbtiScore = childMbtiScoreRepository.findByChildIdAndStatus(child.getId(), ChildMbtiScoreStatus.ACTIVE);

        // 기존 데이터가 있으면 status 값을 NONACTIVE로 변경
        existingChildMbtiScore.ifPresent(ChildMbtiScore -> {
            ChildMbtiScore.updateStatus(ChildMbtiScoreStatus.NONACTIVE);
        });

        // ChildMbtiScore 저장
        ChildMbtiScore childMbtiScore = ChildMbtiScore.builder()
                .eiScore(dto.getM())
                .snScore(dto.getB())
                .tfScore(dto.getT())
                .jpScore(dto.getI())
                .assessmentDate(LocalDate.now())
                .status(ChildMbtiScoreStatus.ACTIVE)
                .child(child)
                .build();

        childMbtiScoreRepository.save(childMbtiScore);
    }

    public void deleteMbti(Long childMbtiScoreId) {
        Optional<ChildMbtiScore> deleteChildMbti = childMbtiScoreRepository.findById(childMbtiScoreId);
        deleteChildMbti.ifPresent(ChildMbtiScore -> {
            ChildMbtiScore.updateStatus(ChildMbtiScoreStatus.DELETE);
        });
    }

}
