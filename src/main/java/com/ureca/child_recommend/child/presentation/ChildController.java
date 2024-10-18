package com.ureca.child_recommend.child.presentation;

import com.ureca.child_recommend.child.application.ChildService;
import com.ureca.child_recommend.child.application.FileService;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<String> createChildProfile(@AuthenticationPrincipal Long userId,
                                                     @RequestBody ChildDto.Request childRequest) {

            childService.createChildProfile(userId, childRequest); // 자녀 프로필 생성
            return ResponseEntity.status(HttpStatus.CREATED).body("Child profile created successfully."); // 성공 메시지 반환
    }

    @GetMapping("/child/{child_id}")
    public ResponseEntity<ChildDto.Response> getChildById(@AuthenticationPrincipal Long userId, @PathVariable("child_id") Long childId) {
        ChildDto.Response childDto = childService.getChildById(userId, childId);
        return ResponseEntity.ok(childDto);
    }

    @PatchMapping("/child/{child_id}")
    public ResponseEntity<ChildDto.Response> updateChild(@AuthenticationPrincipal Long userId,
                                                         @PathVariable("child_id") Long childId,
                                                         @RequestBody ChildDto.Request childRequest) {
        // 수정 처리
        ChildDto.Response updatedChild = childService.updateChild(userId, childId, childRequest);
        return ResponseEntity.ok(updatedChild);
    }

    @DeleteMapping("/child/{child_id}")
    public ResponseEntity<Void> deleteChild(@AuthenticationPrincipal Long userId,
                                            @PathVariable("child_id") Long childId) {

        // 삭제 처리
        childService.deleteChild(userId, childId);
        return ResponseEntity.noContent().build();
    }

}
