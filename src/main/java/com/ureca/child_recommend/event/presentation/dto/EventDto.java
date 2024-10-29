package com.ureca.child_recommend.event.presentation.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class EventDto {

    @Builder
    @Getter
    public static class Request {
        private String name;
        private LocalDate date; // LocalDate 등 적절한 타입으로 수정 가능
        private String description;
    }
}
