package com.ureca.child_recommend.event.application;


import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.domain.LogHistory;
import com.ureca.child_recommend.event.domain.WinnerLog;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.event.infrastructure.LogHistoryRepository;
import com.ureca.child_recommend.event.infrastructure.WinnerLogRepository;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public void moveToHistory() {
        List<WinnerLog> winnerLogs = winnerLogRepository.findAll();
        List<LogHistory> logHistories = new ArrayList<>();
        for(WinnerLog winnerlog : winnerLogs) {
            LogHistory logHistory = LogHistory.builder()
                    .name(winnerlog.getName())
                    .phone(winnerlog.getPhone())
                    .log(winnerlog.getLog())
                    .user(userRepository.findById(winnerlog.getUser().getId()).get())
                    .event(eventRepository.findEventById(winnerlog.getEvent().getId()).get())
                    .build();

            logHistories.add(logHistory);
        }

        logHistoryRepository.saveAll(logHistories);
    }
}
