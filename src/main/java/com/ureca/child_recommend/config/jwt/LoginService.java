package com.ureca.child_recommend.config.jwt;


import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.user.domain.User;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService{

    private final UserRepository userRepository;

    public User findUser(Long id){
        return userRepository.findById(id).orElseThrow(()->new BusinessException(CommonErrorCode.USER_NOT_FOUND));
    }
}