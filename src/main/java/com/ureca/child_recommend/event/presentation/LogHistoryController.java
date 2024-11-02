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

    @Scheduled(cron = "0 0 12 * * ?") // 매일 12시에 합격자, 알람명단 조회
    public SuccessResponse<String> GetAllPeopleToday(){
        List<LogHistory> logHistories = logHistoryService.findTodayApology(); // 전체 명단 찾는 메소드
        List<LogHistory> WinnerHistories = logHistoryService.findTodayWinner(logHistories); // 합격자 명단 얻어 오는 메소드
        return SuccessResponse.successWithoutResult(null);
    }
    


}
