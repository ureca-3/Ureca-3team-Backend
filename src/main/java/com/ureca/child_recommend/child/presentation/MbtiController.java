package com.ureca.child_recommend.child.presentation;

import com.ureca.child_recommend.child.application.MbtiService;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import com.ureca.child_recommend.child.presentation.dto.MbtiDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MbtiController {

    private final MbtiService mbtiService;

    // 진단하기
    @PostMapping("/assessment/{child_id}")
    public SuccessResponse<MbtiDto.Response.assessmentMbtiDto> assessmentMbti(@AuthenticationPrincipal Long userId, @RequestBody MbtiDto.Request.assessmentMbtiDto dto, @PathVariable("child_id") Long child_id) {
        // MBTI 계산 및 저장
        MbtiDto.Response.assessmentMbtiDto resultDto = mbtiService.saveMbtiResult(userId, dto, child_id);
        return SuccessResponse.success(resultDto);
    }

    // 진단 삭제
    @PatchMapping("/assessment/{childMbtiScore_id}")
    public SuccessResponse<Long> deleteAssessmentMbti(@PathVariable("childMbtiScore_id") Long childMbtiScore_id) {
        mbtiService.deleteMbti(childMbtiScore_id);
        return SuccessResponse.success(childMbtiScore_id);
    }

    /**
     * 24.10.29 작성자 : 정주현
     * 자녀의 진단 내역 조회
     */
    @GetMapping("/assessment/{child_id}")
    public SuccessResponse<List<MbtiDto.Response.assessmentMbtiResultDto>> getAssessmentMbtiResults(@AuthenticationPrincipal Long userId,@PathVariable("child_id") Long childId){
        List<MbtiDto.Response.assessmentMbtiResultDto> response =  mbtiService.getAssessmentMbtiResults(userId,childId);
        return SuccessResponse.success(response);

    }
}




