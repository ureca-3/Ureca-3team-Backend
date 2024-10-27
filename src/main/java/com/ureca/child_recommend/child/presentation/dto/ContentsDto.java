package com.ureca.child_recommend.child.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ContentsDto {

    public static class Response{

        @Getter
        @AllArgsConstructor
        @Builder
        public static class SimilarUserDto{
            private Long chiild_id;
            private Float similarity;
        }
        @Getter
        @Builder
        public static class SimilarBookDto{
            private Long bookId;
            private String title;
            private String profileUrl;

            public static SimilarBookDto of(Long bookId,String title,String profileUrl){
                return SimilarBookDto.builder()
                        .bookId(bookId)
                        .title(title)
                        .profileUrl(profileUrl)
                        .build();
            }
        }


    }
    public static class Request{
        @Getter
        @Builder
        public static class enrollToContents {
            private Long childId;
            private float[] embedding;  // 벡터를 double 배열로 저장
        }
    }

}
