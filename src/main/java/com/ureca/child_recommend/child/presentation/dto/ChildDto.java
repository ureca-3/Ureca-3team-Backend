package com.ureca.child_recommend.child.presentation.dto;

import com.ureca.child_recommend.child.domain.Child;
import jakarta.annotation.Nullable;
import lombok.*;

import java.time.LocalDate;

public class ChildDto {

    // 요청 DTO
    @Getter
    @Builder
    public static class Request {
        @Nullable // 필수 아님을 명시
        private String name;

        @Nullable
        private String gender;

        @Nullable
        private LocalDate birthday;

        @Nullable
        private String profileUrl;

        @Nullable
        private Integer age; // 나이 추가


        // 기본 생성자는 Lombok이 제공
    }

    // 응답 DTO
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PUBLIC) // public 생성자 추가
    public static class Response {
        private String name;
        private String gender;
        private LocalDate birthday;
        private String profileUrl;



        public static Response fromEntity(Child child) {
            return Response.builder()
                    .name(child.getName())
                    .gender(child.getGender())
                    .birthday(child.getBirthday())
                    .profileUrl(child.getProfileUrl())
                    .build();
        }
    }
}
