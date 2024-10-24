package com.ureca.child_recommend.contents.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatDto {
    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "메세지를 입력해주세요.")
        private String message;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private String message;
    }
}
