package com.ureca.child_recommend.event.presentation;


import com.ureca.child_recommend.event.application.LogHistoryService;
import com.ureca.child_recommend.event.domain.LogHistory;
import com.ureca.child_recommend.event.presentation.dto.EventDto;
import com.ureca.child_recommend.event.presentation.dto.LogHistoryDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LogHistoryController {

    private final LogHistoryService logHistoryService;

    @Scheduled(cron = "0 0 00 * * ?") // 매일 00시에 삭제
    public SuccessResponse<String> deleteLog(){
        List<LogHistory> logHistories = logHistoryService.findLogDate();
        logHistoryService.deleteBeforeLog(logHistories);
        return SuccessResponse.successWithoutResult(null);
    }


}
