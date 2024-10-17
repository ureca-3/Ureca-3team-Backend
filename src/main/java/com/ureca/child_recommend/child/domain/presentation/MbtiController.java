package com.ureca.child_recommend.child.domain.presentation;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.application.ChildService;
import com.ureca.child_recommend.child.domain.application.MbtiService;
import com.ureca.child_recommend.child.domain.presentation.dto.MbtiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mbti")
public class MbtiController {

    @Autowired
    MbtiService mbtiService;
    @Autowired
    ChildService childService;

    // 진단하기
    @PostMapping("/assessment/{childId}")
    public ResponseEntity<String> assessmentMbti(@RequestBody MbtiDto mbtiDto, @PathVariable("childId") Long childId) {

        Child child = childService.getChildById(childId);
        // MBTI 계산 및 저장
        mbtiService.saveMbtiResult(mbtiDto, child);

        return ResponseEntity.ok("MBTI Result: " + mbtiDto.getResult());
    }

    // 진단 삭제
    @DeleteMapping("/assessment/{childMbtiScore_id}")
    public ResponseEntity<String> deleteAssessmentMbti(@PathVariable("childMbtiScore_id") Long childMbtiScore_id) {
        mbtiService.deleteMbti(childMbtiScore_id);
        return ResponseEntity.ok("삭제 성공. childMbtiScore_id : " + childMbtiScore_id);

    }

}
