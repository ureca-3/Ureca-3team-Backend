package com.ureca.child_recommend.config.oauth;


import com.ureca.child_recommend.config.oauth.client.Helper.KakaoOauthHelper;
import com.ureca.child_recommend.config.oauth.client.KakaoOauthClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Getter
public class OauthConfig {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    // KakaoOauthHelper 빈으로 등록
    @Bean
    public KakaoOauthHelper kakaoOauthHelper(OAuthOIDCHelper oauthOIDCHelper, KakaoOauthClient kakaoOauthClient) {
        return new KakaoOauthHelper(oauthOIDCHelper, kakaoOauthClient, kakaoClientId, kakaoRedirectUri);
    }

}
