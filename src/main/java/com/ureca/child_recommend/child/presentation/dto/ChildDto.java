package com.ureca.child_recommend.child.presentation.dto;

import com.ureca.child_recommend.child.domain.Child;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class ChildDto {

    // 요청 DTO
    @Getter
    @Builder
    public static class Request {
        private String name;
        private String gender;
        private LocalDate birthday;
        private String profileUrl;

        // 기본 생성자는 Lombok이 제공
    }

    // 응답 DTO
    @Getter
    @Builder
    public static class Response {
        private String name;
        private String gender;
        private LocalDate birthday;
        private String profileUrl;
        private Long userId;

        public static Response fromEntity(Child child) {
            return Response.builder()
                    .name(child.getName())
                    .gender(child.getGender())
                    .birthday(child.getBirthday())
                    .profileUrl(child.getProfileUrl())
                    .build();
        }

        public Long getUserId() {
            return userId; // 부모의 userId 반환
        }
    }
}
