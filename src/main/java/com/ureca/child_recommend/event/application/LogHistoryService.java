package com.ureca.child_recommend.event.application;


import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import com.ureca.child_recommend.event.domain.Event;
import com.ureca.child_recommend.event.domain.LogHistory;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.event.infrastructure.LogHistoryRepository;
import com.ureca.child_recommend.event.presentation.dto.LogHistoryDto;
import com.ureca.child_recommend.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LogHistoryService {

    private final LogHistoryRepository logHistoryRepository;
    private final EventRepository eventRepository;

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

    public List<LogHistory> findTodayApology() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 전날 날짜에 해당하는 이벤트 조회
        List<Event> events = eventRepository.findAllByDate(yesterday); // 이벤트를 날짜로 조회하는 메서드 필요

        List<LogHistory> logHistories = logHistoryRepository.findAllByEventIn(events);

        return logHistories;
    }

    public List<LogHistory> findTodayWinner(List<LogHistory> logHistories) {
        List<LogHistory> winnerLogs =  logHistories.stream()
                                       .filter(log -> log.getStatus() == ApplyLogStatus.WINNER)
                                       .collect(Collectors.toList());
        return winnerLogs;
    }




}
