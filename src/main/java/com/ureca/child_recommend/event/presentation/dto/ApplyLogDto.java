package com.ureca.child_recommend.event.presentation.dto;

import com.ureca.child_recommend.event.domain.ApplyLog;
import com.ureca.child_recommend.event.domain.Enum.ApplyLogStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApplyLogDto {
    @Builder
    @Getter
    public static class Request {
        private String name;
        private String phone;
    }

    @Builder
    @Getter
    public static class Response {
        private Long id;
        private String name;
        private String phone;
        private LocalDateTime log;
        private ApplyLogStatus status;


        public static Response from(ApplyLog applyLog) {
            return new Response(
                    applyLog.getUser().getId(),
                    applyLog.getName(),
                    applyLog.getPhone(),
                    applyLog.getLog(),
                    applyLog.getStatus()
            );
        }
    }


}
