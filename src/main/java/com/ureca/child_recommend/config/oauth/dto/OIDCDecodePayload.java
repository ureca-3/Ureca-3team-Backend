package com.ureca.child_recommend.config.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OIDCDecodePayload {
    private String iss; //카카오 인증 서버에서 발행한 토큰
    private String aud; //애플리케이션 식별자
    private String sub; //사용자 고유식별자
    private String nickname;
    private String profile;
    private String email;
    private String gender;
    private String age_range;
}
