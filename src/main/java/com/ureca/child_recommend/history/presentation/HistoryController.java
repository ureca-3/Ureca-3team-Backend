package com.ureca.child_recommend.history.presentation;

import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.history.application.HistoryService;
import com.ureca.child_recommend.history.presentation.dto.MbtiHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/history/{child_id}")
    public SuccessResponse<MbtiHistoryDto.Response.HistoryDto> getHistory(@PathVariable("child_id") Long child_id, @RequestParam("type") String type) {
        MbtiHistoryDto.Response.HistoryDto data = historyService.getHistoryData(child_id, type);
        return SuccessResponse.success(data);
    }

}
