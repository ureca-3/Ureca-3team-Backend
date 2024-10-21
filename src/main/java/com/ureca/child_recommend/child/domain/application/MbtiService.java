package com.ureca.child_recommend.child.domain.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbti;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiStatus;
import com.ureca.child_recommend.child.domain.infrastructure.ChildMbtiRepository;
import com.ureca.child_recommend.child.domain.infrastructure.ChildMbtiScoreRepository;
import com.ureca.child_recommend.child.domain.presentation.dto.MbtiDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MbtiService {

    private final ChildService childService;

    private final ChildMbtiRepository childMbtiRepository;

    private final ChildMbtiScoreRepository childMbtiScoreRepository;

    @Transactional
    public MbtiDto.Response.assessmentMbtiDto saveMbtiResult(MbtiDto.Request.assessmentMbtiDto dto, Long child_id) {

        String result = calculateMbti(dto);
        Child child = childService.getChildById(child_id);

        // 1. 자녀 PK로 ChildMbti 데이터 조회
        Optional<ChildMbti> existingChildMbti = childMbtiRepository.findByChildIdAndStatus(child.getId(), ChildMbtiStatus.ACTIVE);

        // 2. 기존 데이터가 있으면 status 값을 NONACTIVE로 변경
        existingChildMbti.ifPresent(childMbti -> {
            childMbti.updateStatus(ChildMbtiStatus.NONACTIVE);
        });

        // ChildMbti 저장
        childMbtiRepository.save(ChildMbti.enrollToMbti(result, child));

        // 1. 자녀 PK로 ChildMbti 데이터 조회
        Optional<ChildMbtiScore> existingChildMbtiScore = childMbtiScoreRepository.findByChildIdAndStatus(child.getId(), ChildMbtiScoreStatus.ACTIVE);

        // 2. 기존 데이터가 있으면 status 값을 NONACTIVE로 변경
        existingChildMbtiScore.ifPresent(ChildMbtiScore -> {
            ChildMbtiScore.updateStatus(ChildMbtiScoreStatus.NONACTIVE);
        });

        childMbtiScoreRepository.save(ChildMbtiScore.enrollToMbtiScore(dto.getM(),dto.getB(), dto.getT(), dto.getI(), child));
        return MbtiDto.Response.assessmentMbtiDto.of(result);
    }

    @Transactional
    public void deleteMbti(Long childMbtiScoreId) {
        ChildMbtiScore deleteChildMbti = childMbtiScoreRepository.findById(childMbtiScoreId).orElseThrow(()->new BusinessException(CommonErrorCode.ASSESSMENT_NOT_FOUND));
        deleteChildMbti.updateStatus(ChildMbtiScoreStatus.DELETE);

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
}
