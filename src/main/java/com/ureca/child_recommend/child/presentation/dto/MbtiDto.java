package com.ureca.child_recommend.child.presentation.dto;

import lombok.Builder;
import lombok.Getter;

public class MbtiDto {

    public static class Response{
        @Builder
        @Getter
        public static class assessmentMbtiDto{
            private String mbti;

            public static assessmentMbtiDto of(String mbti){
                return assessmentMbtiDto.builder()
                        .mbti(mbti)
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
