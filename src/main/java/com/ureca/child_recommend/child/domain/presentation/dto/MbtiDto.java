package com.ureca.child_recommend.child.domain.presentation.dto;

import lombok.Builder;
import lombok.Getter;

public class MbtiDto {

    public static class Response{
        @Builder
        @Getter
        public static class assessmentMbtiDto{
            private String result;

            public static assessmentMbtiDto of(String result){
                return assessmentMbtiDto.builder()
                        .result(result)
                        .build();
            }
        }

    }
    public static class Request{
        @Getter
        public static class assessmentMbtiDto {
            private int m;
            private int b;
            private int t;
            private int i;
        }
    }
}
