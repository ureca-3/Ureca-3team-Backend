package com.ureca.child_recommend.user.dto;

import lombok.Builder;
import lombok.Getter;

public class UserDto {

    public static class Response{
        @Builder
        @Getter
        public static class SignIn{
            private String accessToken;
            private String refreshToken;

            public static SignIn of(String accessToken,String refreshToken){
                return SignIn.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }

        @Builder
        @Getter
        public static class Reissue{
            private String accessToken;
            private String refreshToken;

            public static Reissue of(String accessToken,String refreshToken){
                return Reissue.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
    }

    @Builder
    @Getter
    public static class Request{
        private String nickname;
        private String email;
        private String gender;
        private String ageRange;

    }
}
