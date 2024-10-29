package com.ureca.child_recommend.child.presentation.dto;

import com.ureca.child_recommend.child.domain.ChildMbtiScore;
import com.ureca.child_recommend.child.domain.Enum.ChildMbtiScoreStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

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

        @Builder
        @Getter
        public static class assessmentMbtiResultDto{
            private Long childMbtiScoreId;
            private LocalDate assessmentDate;
            private int eiScore;
            private int jpScore;
            private int snScore;
            private int tfScore;
            private String mbtiResult;
            private ChildMbtiScoreStatus status;
            public static assessmentMbtiResultDto of(ChildMbtiScore childMbtiScore, String mbtiResult){
                return assessmentMbtiResultDto.builder()
                        .childMbtiScoreId(childMbtiScore.getId())
                        .assessmentDate(childMbtiScore.getAssessmentDate())
                        .eiScore(childMbtiScore.getEiScore())
                        .jpScore(childMbtiScore.getJpScore())
                        .snScore(childMbtiScore.getSnScore())
                        .tfScore(childMbtiScore.getTfScore())
                        .mbtiResult(mbtiResult)
                        .status(childMbtiScore.getStatus())
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
