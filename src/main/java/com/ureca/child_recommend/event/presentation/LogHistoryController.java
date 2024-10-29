package com.ureca.child_recommend.event.presentation;


import com.ureca.child_recommend.event.application.LogHistoryService;
import com.ureca.child_recommend.event.domain.Enum.LogHistoryStatus;
import com.ureca.child_recommend.event.presentation.dto.EventDto;
import com.ureca.child_recommend.event.presentation.dto.LogHistoryDto;
import com.ureca.child_recommend.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LogHistoryController {

    private final LogHistoryService logHistoryService;

    @PatchMapping("/event/status")
    public SuccessResponse<String> patchLog(@RequestBody LogHistoryDto.Request historyRequest){
        logHistoryService.patchLogHistoryStatus(historyRequest);
        return SuccessResponse.successWithoutResult(null);
    }
}
