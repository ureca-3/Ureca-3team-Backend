package com.ureca.child_recommend.event.application;


import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.domain.LogHistory;
import com.ureca.child_recommend.event.domain.WinnerLog;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.event.infrastructure.LogHistoryRepository;
import com.ureca.child_recommend.event.infrastructure.WinnerLogRepository;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.LOG_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WinnerLogService {

    private final WinnerLogRepository winnerLogRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final LogHistoryRepository logHistoryRepository;

    @Transactional
    public void deleteAllLog() {
        winnerLogRepository.deleteAll();
    }
}
