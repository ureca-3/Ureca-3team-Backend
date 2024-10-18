package com.ureca.child_recommend.child.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.infrastructure.ChildRepository;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import com.ureca.child_recommend.user.domain.User;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildService childService;
    private final UserRepository userRepository;

    public ChildDto.Response findChildById(Long childId) {
        // childId로 Child를 찾아서 없으면 예외 던지기
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("Child not found with id: " + childId));

        // Child를 ChildDto.Response로 변환하여 반환
        return ChildDto.Response.fromEntity(child);
    }

    public void createChildProfile(Long userId, ChildDto.Request childRequest) {
        // userId로 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 도메인의 createChildProfile 메서드 호출
        Child child = Child.createChildProfile(
                childRequest.getName(),
                childRequest.getGender(),
                childRequest.getBirthday(),
                childRequest.getProfileUrl(),
                user
        );

        // 자녀 프로필 저장
        childRepository.save(child);
    }


    // 자녀 조회
    public ChildDto.Response getChildById(Long userId, Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        ChildDto.Response Child = childService.findChildById(childId); // childId로 Child 조회
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("부모가 아닙니다.");} // Response DTO로 변환하여 반환
        return ChildDto.Response.fromEntity(child);
    }


    // 자녀 수정
    public ChildDto.Response updateChild(Long userId, Long childId, ChildDto.Request childRequest) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        // 요청 DTO에서 정보를 가져와 업데이트
        ChildDto.Response Child = childService.findChildById(childId); // childId로 Child 조회
        if (child.getUser().getId().equals(userId)) {child.updateChildInfo(childRequest.getName(),
                childRequest.getGender(), childRequest.getBirthday(), childRequest.getProfileUrl());}
        childRepository.save(child);

        return ChildDto.Response.fromEntity(child); // 업데이트된 정보를 Response DTO로 변환하여 반환
    }


    // 자녀 삭제
    public void deleteChild(Long userId, Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        // Child의 부모가 현재 로그인한 user인지 확인
        ChildDto.Response Child = childService.findChildById(childId); // childId로 Child 조회
        if (child.getUser().getId().equals(userId)) {childRepository.delete(child);}
    }

}
