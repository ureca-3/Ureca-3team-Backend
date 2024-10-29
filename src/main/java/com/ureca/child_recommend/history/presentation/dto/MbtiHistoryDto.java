package com.ureca.child_recommend.history.presentation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class MbtiHistoryDto {
    public static class Response {
        @Builder
        @Getter
        public static class HistoryDto {
            private List<Integer> eiScore;  // E/I 점수 리스트
            private List<Integer> snScore;  // S/N 점수 리스트
            private List<Integer> tfScore;  // T/F 점수 리스트
            private List<Integer> jpScore;  // J/P 점수 리스트
            private List<String> dayList;

            public static HistoryDto of(List<Integer> eiScore, List<Integer> snScore, List<Integer> tfScore, List<Integer> jpScore, List<String> dayList) {
                return HistoryDto.builder()
                        .eiScore(eiScore)
                        .snScore(snScore)
                        .tfScore(tfScore)
                        .jpScore(jpScore)
                        .dayList(dayList)
                        .build();
            }
        }
    }
}

