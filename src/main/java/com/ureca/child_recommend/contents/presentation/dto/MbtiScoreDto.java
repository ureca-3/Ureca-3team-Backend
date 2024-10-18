package com.ureca.child_recommend.contents.presentation.dto;

import com.ureca.child_recommend.contents.domain.ContentsMbtiScore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MbtiScoreDto {
    @Getter @Builder
    public static class Response {
        private Long id;
        private Integer eiScore;
        private Integer snScore;
        private Integer tfScore;
        private Integer jpScore;

        public static Response MbtiScoreData(ContentsMbtiScore contentsMbtiScore) {
            return Response.builder()
                    .id(contentsMbtiScore.getId())
                    .eiScore(contentsMbtiScore.getEiScore())
                    .snScore(contentsMbtiScore.getSnScore())
                    .tfScore(contentsMbtiScore.getTfScore())
                    .jpScore(contentsMbtiScore.getJpScore())
                    .build();
        }
    }

    @Getter @NoArgsConstructor
    public static class Request {
        private Integer eiScore;
        private Integer snScore;
        private Integer tfScore;
        private Integer jpScore;

        @Getter @NoArgsConstructor
        public static class dataId {
            private Long id;
        }
    }
}
