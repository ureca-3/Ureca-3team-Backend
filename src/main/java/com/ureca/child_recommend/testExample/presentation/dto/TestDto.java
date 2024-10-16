package com.ureca.child_recommend.testExample.presentation.dto;

import lombok.Builder;
import lombok.Getter;

public class TestDto {

    public static class Response{
        @Builder
        @Getter
        public static class test{
            private String text;

            public static test of(String text){
                return test.builder()
                        .text(text)
                        .build();
            }
        }

    }
    public static class Request{
        @Getter
        public static class test {
            private Long id;
        }
    }

    }
