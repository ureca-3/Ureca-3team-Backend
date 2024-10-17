package com.ureca.child_recommend.child.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.infrastructure.ChildRepository;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import com.ureca.child_recommend.user.domain.User;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final UserRepository userRepository;

    public void createChildProfile(Long userId, ChildDto.Request childRequest, String profileUrl) {
        // userId로 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 도메인의 createChildProfile 메서드 호출
        Child child = Child.createChildProfile(
                childRequest.getName(),
                childRequest.getGender(),
                childRequest.getBirthday(),
                profileUrl,
                user
        );

        // 자녀 프로필 저장
        childRepository.save(child);
    }


    // 자녀 조회
    public ChildDto.Response getChildById(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));
        return ChildDto.Response.fromEntity(child); // Response DTO로 변환하여 반환
    }


    // 자녀 수정
    public ChildDto.Response updateChild(Long childId, ChildDto.Request childRequest) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        // 요청 DTO에서 정보를 가져와 업데이트
        child.updateChildInfo(childRequest.getName(), childRequest.getGender(), childRequest.getBirthday(), childRequest.getProfileUrl());
        childRepository.save(child);

        return ChildDto.Response.fromEntity(child); // 업데이트된 정보를 Response DTO로 변환하여 반환
    }


    // 자녀 삭제
    public void deleteChild(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));
        childRepository.delete(child);
    }
}
