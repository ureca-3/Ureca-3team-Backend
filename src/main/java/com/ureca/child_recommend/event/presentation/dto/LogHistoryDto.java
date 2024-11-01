package com.ureca.child_recommend.event.presentation.dto;

import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import lombok.Builder;
import lombok.Getter;

public class LogHistoryDto {

    @Builder
    @Getter
    public static class Request {
        private Long logId;
        private ApplyLogStatus status;
    }
}
