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

    // 자녀 프로필 생성 API
    @PostMapping("/child")
    public ResponseEntity<String> createChildProfile(@RequestParam Long userId,
                                                     @RequestParam MultipartFile file,
                                                     @RequestBody ChildDto.Request childRequest) {
        try {
            String profileUrl = fileService.storeFile(file); // 파일 저장 및 URL 생성
            childService.createChildProfile(userId, childRequest, profileUrl); // 자녀 프로필 생성
            return ResponseEntity.status(HttpStatus.CREATED).body("Child profile created successfully."); // 성공 메시지 반환
        } catch (IOException e) {
            // IOException 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    // IOException을 처리하는 메서드 (필요 시 추가)
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File error: " + e.getMessage());
    }

    @GetMapping("/child/{child_id}")
    public ResponseEntity<ChildDto.Response> getChildById(@PathVariable("child_id") Long childId) {
        ChildDto.Response childDto = childService.getChildById(childId);
        return ResponseEntity.ok(childDto);
    }

    @PatchMapping("/child/{child_id}")
    public ResponseEntity<ChildDto.Response> updateChild(@PathVariable("child_id") Long childId,
                                                         @RequestBody ChildDto.Request childRequest) {
        ChildDto.Response updatedChild = childService.updateChild(childId, childRequest);
        return ResponseEntity.ok(updatedChild);
    }

    @DeleteMapping("/child/{child_id}")
    public ResponseEntity<Void> deleteChild(@PathVariable("child_id") Long childId) {
        childService.deleteChild(childId);
        return ResponseEntity.noContent().build();
    }
}
