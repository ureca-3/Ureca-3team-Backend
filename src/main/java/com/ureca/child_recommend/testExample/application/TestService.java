package com.ureca.child_recommend.testExample.application;

import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.testExample.infrastructure.TestRepository;
import com.ureca.child_recommend.testExample.presentation.dto.TestDto;
import com.ureca.child_recommend.user.domain.User;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final UserRepository testRepository;
    public TestDto.Response.test test(Long userId){
        User user = testRepository.findById(userId).orElseThrow(()->new BusinessException(CommonErrorCode.TEST_NOT_FOUND));

        String testString = user.test();

        return TestDto.Response.test.of(testString);
    }
}
