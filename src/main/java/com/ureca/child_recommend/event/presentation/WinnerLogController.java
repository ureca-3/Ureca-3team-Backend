package com.ureca.child_recommend.event.presentation;


import com.ureca.child_recommend.event.application.WinnerLogService;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WinnerLogController {

    private final WinnerLogService winnerLogService;


//    @Scheduled(cron = "0 50 16 * * ?") // 응모로그 삭제
    public SuccessResponse<String> deleteWinnerLog() {
        winnerLogService.moveToHistory();
        winnerLogService.deleteAllLog();
        return SuccessResponse.successWithoutResult(null);
    }
}
