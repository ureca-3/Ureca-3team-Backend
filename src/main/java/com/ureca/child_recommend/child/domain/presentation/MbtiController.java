package com.ureca.child_recommend.child.domain.presentation;

import com.ureca.child_recommend.child.domain.application.MbtiService;
import com.ureca.child_recommend.child.domain.presentation.dto.MbtiDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MbtiController {

    private final MbtiService mbtiService;

    // 진단하기
    @PostMapping("/assessment/{child_id}")
    public SuccessResponse<MbtiDto.Response.assessmentMbtiDto> assessmentMbti(@RequestBody MbtiDto.Request.assessmentMbtiDto dto, @PathVariable("child_id") Long child_id) {
        // MBTI 계산 및 저장
        MbtiDto.Response.assessmentMbtiDto resultDto = mbtiService.saveMbtiResult(dto, child_id);
        return SuccessResponse.success(resultDto);
    }

    // 진단 삭제
    @PatchMapping("/assessment/{childMbtiScore_id}")
    public SuccessResponse<Long> deleteAssessmentMbti(@PathVariable("childMbtiScore_id") Long childMbtiScore_id) {
        mbtiService.deleteMbti(childMbtiScore_id);
        return SuccessResponse.success(childMbtiScore_id);
    }

}




