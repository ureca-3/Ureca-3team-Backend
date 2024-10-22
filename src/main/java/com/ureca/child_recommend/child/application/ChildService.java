package com.ureca.child_recommend.child.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.ChildMbti;
import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.infrastructure.ChildMbtiScoreRepository;
import com.ureca.child_recommend.child.infrastructure.ChildRepository;
import com.ureca.child_recommend.child.presentation.dto.ChildDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.user.domain.User;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMbtiScoreRepository childMbtiScoreRepository;
    private final UserRepository userRepository;

    public ChildDto.Response findChildById(Long childId) {
        // childId로 Child를 찾아서 없으면 예외 던지기
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));

        // Child를 ChildDto.Response로 변환하여 반환
        return ChildDto.Response.fromEntity(child);
    }

    @Transactional
    public void createChildProfile(Long userId, ChildDto.Request childRequest) {
        // userId로 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));

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
    public List<ChildDto.Response> getAllChildren() {
        List<Child> children = childRepository.findAll();  // 전체 조회
        return children.stream()
                .map(ChildDto.Response::fromEntity) // fromEntity 메서드로 변환
                .collect(Collectors.toList());
    }



    // 자녀 상세 조회
    public ChildDto.Response getChildById(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));
        return ChildDto.Response.fromEntity(child);
    }


    // 자녀 수정
    @Transactional
    public ChildDto.Response updateChild(Long childId, ChildDto.Request childRequest) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));

        // 요청 DTO에서 정보를 가져와 업데이트
       child.updateChildInfo(childRequest.getName(),
                childRequest.getGender(), childRequest.getBirthday(), childRequest.getProfileUrl());

        return ChildDto.Response.fromEntity(child); // 업데이트된 정보를 Response DTO로 변환하여 반환
    }


    // 자녀 삭제
    public void deleteChild(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));
        Child.updateChildStatus(child);
    }

    public ChildMbtiScore getChildMbti(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));

        // ChildMbtiScore를 조회
        ChildMbtiScore childMbtiScore = childMbtiScoreRepository.findByChild(child)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));

        return childMbtiScore; // 또는 필요한 값을 반환
    }
}
