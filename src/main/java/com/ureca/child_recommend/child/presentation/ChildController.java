package com.ureca.child_recommend.child.presentation;

import com.ureca.child_recommend.child.application.ChildService;
import com.ureca.child_recommend.child.application.FileService;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChildController {
    private final ChildService childService;
    private final FileService fileService;

    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfile(@RequestParam MultipartFile file) {
        try {
            // 파일 저장 및 URL 반환
            String fileUrl = fileService.storeFile(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    // 자녀 프로필 생성 API
    @PostMapping("/child")
    public SuccessResponse<String> createChildProfile(@AuthenticationPrincipal Long userId,
                                                      @RequestBody ChildDto.Request childRequest) {

            childService.createChildProfile(userId, childRequest); // 자녀 프로필 생성
            return SuccessResponse.successWithoutResult(null); // 성공 메시지 반환
    }

    @GetMapping("/child") // 전체 조회
    public SuccessResponse<List<ChildDto.Response>> getAllChildren() {
        List<ChildDto.Response> childList = childService.getAllChildren();
        return SuccessResponse.success(childList);
    }

    @GetMapping("/child/{child_id}")//상세조회
    public SuccessResponse<ChildDto.Response> getChildById(@PathVariable("child_id") Long childId) {
        ChildDto.Response childDto = childService.getChildById( childId);
        return SuccessResponse.success(childDto);
    }

    @PatchMapping("/child/{child_id}")
    public SuccessResponse<ChildDto.Response> updateChild( @PathVariable("child_id") Long childId,
                                                         @RequestBody ChildDto.Request childRequest) {
        // 수정 처리
        ChildDto.Response updatedChild = childService.updateChild(childId, childRequest);
        return SuccessResponse.success(updatedChild);
    }


    @DeleteMapping("/child/{child_id}")
    public SuccessResponse<String> deleteChild(@PathVariable("child_id") Long childId) {
        // 삭제 처리
        childService.deleteChild(childId);
        return SuccessResponse.successWithoutResult(null);
    }

}
