package com.ureca.child_recommend.contents.presentation.dto;

import lombok.Builder;
import lombok.Getter;

public class BookDto {

    public static class Response {
        @Builder
        @Getter
        public static class RegisterBookResponse {
            private String message;

            public static RegisterBookResponse of(String message){
                return RegisterBookResponse.builder()
                        .message(message).build();
            }
        }
    }
    public static class Request {
        @Getter
        public static class RegisterBookRequest {
            private String title;
        }
    }

}
