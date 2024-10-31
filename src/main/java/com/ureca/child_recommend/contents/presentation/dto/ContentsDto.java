package com.ureca.child_recommend.contents.presentation.dto;

import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.contents.domain.ContentsMbtiScore;
import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class ContentsDto {

    @Getter @Builder
    public static class Response {
        private Long id;
        private String title;
        private String posterUrl;
        private String description;
        private String author;
        private String publisher;
        private LocalDate publicationYear;
        private String contentsMbtiResult;
        private ContentsStatus status;
        private ContentsMbtiScore contentsMbti;

        public static Response contentsData(Contents contents, ContentsMbtiScore mbtiScore) {
            return Response.builder()
                    .id(contents.getId())
                    .title(contents.getTitle())
                    .posterUrl(contents.getPosterUrl())
                    .description(contents.getDescription())
                    .author(contents.getAuthor())
                    .publisher(contents.getPublisher())
                    .publicationYear(contents.getPublicationYear())
                    .contentsMbtiResult(contents.getContentsMbtiResult())
                    .status(contents.getStatus())
                    .contentsMbti(mbtiScore)
                    .build();
        }
    }

    @Getter @NoArgsConstructor
    public static class Request {
        private String title;
        private String posterUrl;
        private String description;
        private String author;
        private String publisher;
        private LocalDate publicationYear;
        private String contentsMbtiResult;
        private ContentsStatus status;
        private ContentsMbtiScore contentsMbti;

    }
}
