package com.ureca.child_recommend.event.presentation;

import com.ureca.child_recommend.event.application.ApplyLogService;
import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import com.ureca.child_recommend.event.infrastructure.ApplyLogRepository;
import com.ureca.child_recommend.event.infrastructure.EventRepository;
import com.ureca.child_recommend.event.presentation.dto.ApplyLogDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ApplyLogController {

    private final ApplyLogService applyLogService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ApplyLogRepository applyLogRepository;

    @PostMapping("/event/apply") // 응모 api
    public SuccessResponse<ApplyLogDto.Response> createApplyLog(@RequestBody ApplyLogDto.Request requestDto,
                                                                @AuthenticationPrincipal Long userId) {

        LocalDateTime now = LocalDateTime.now();
        ApplyLogDto.Response responseDto = applyLogService.checkAndRegisterUserId(userId, requestDto, now);

        return SuccessResponse.success(responseDto);
    }



    //    @Scheduled(cron = "0 0 17 * * ?") // 합격자 선별,테이블 이동, 이후 로그 삭제 / 매일 17시에 실행
    public SuccessResponse<String> findWinner(){
        List<ApplyLog> winnerLog = applyLogService.setLogStatus();
        applyLogService.moveLogHistory();
        applyLogService.deleteAllLog();
        applyLogService.removeAllUserIds();
        return SuccessResponse.successWithoutResult(null);
    }

//    @Scheduled(cron = "0 0 12 * * ?") // 응모로그 삭제
//    public SuccessResponse<String> deleteApplyLog() {
//        applyLogService.removeAllUserIds();
//        return SuccessResponse.successWithoutResult(null);
//    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("Health check passed");
    }
}

