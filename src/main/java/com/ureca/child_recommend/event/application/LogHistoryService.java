package com.ureca.child_recommend.event.application;


import com.ureca.child_recommend.event.domain.LogHistory;
import com.ureca.child_recommend.event.infrastructure.LogHistoryRepository;
import com.ureca.child_recommend.event.presentation.dto.LogHistoryDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LogHistoryService {

    private final LogHistoryRepository logHistoryRepository;

    @Transactional
    public List<LogHistory> findLogDate() {
        LocalDateTime log = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        log = log.minus(1,ChronoUnit.DAYS);

        List<LogHistory> logHistories = logHistoryRepository.findAllByLog(log);

        return logHistories;
    }

    @Transactional
    public void deleteBeforeLog(List<LogHistory> logHistories) {
        if (logHistories == null || logHistories.isEmpty()) {
            throw new BusinessException(LOG_NOT_FOUND);
        }

        try {
            logHistoryRepository.deleteAll(logHistories);
        } catch (Exception e) {
            throw new BusinessException(LOG_DELETE_ERROR);
        }
    }
}
