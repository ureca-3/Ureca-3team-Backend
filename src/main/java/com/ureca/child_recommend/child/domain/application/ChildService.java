package com.ureca.child_recommend.child.domain.application;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.child.domain.infrastructure.ChildRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;

    public Child getChildById(Long childId) {
        return childRepository.findById(childId).orElseThrow(()->new BusinessException(CommonErrorCode.CHILD_NOT_FOUND));
    }
}
