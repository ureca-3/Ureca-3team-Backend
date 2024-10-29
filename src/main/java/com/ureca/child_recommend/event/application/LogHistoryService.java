package com.ureca.child_recommend.event.application;


import com.ureca.child_recommend.event.domain.Enum.LogHistoryStatus;
import com.ureca.child_recommend.event.domain.LogHistory;
import com.ureca.child_recommend.event.infrastructure.LogHistoryRepository;
import com.ureca.child_recommend.event.presentation.dto.LogHistoryDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LogHistoryService {

    private final LogHistoryRepository logHistoryRepository;

    @Transactional
    public void patchLogHistoryStatus(LogHistoryDto.Request historyRequest) {
        LogHistory logHistory = logHistoryRepository.findById(historyRequest.getLogId())
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        // 엔티티 메서드를 호출하여 상태 업데이트
        logHistory.updateStatus(historyRequest.getStatus());
    }
}
