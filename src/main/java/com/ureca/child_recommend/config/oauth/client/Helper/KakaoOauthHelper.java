package com.ureca.child_recommend.config.oauth.client.Helper;

import com.ureca.child_recommend.config.oauth.OAuthOIDCHelper;
import com.ureca.child_recommend.config.oauth.dto.OauthInfo;
import com.ureca.child_recommend.config.oauth.client.KakaoOauthClient;
import com.ureca.child_recommend.config.oauth.dto.OIDCDecodePayload;
import com.ureca.child_recommend.config.oauth.dto.OIDCPublicKeysResponse;
import com.ureca.child_recommend.user.domain.Enum.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class KakaoOauthHelper {
    private final OAuthOIDCHelper oauthOIDCHelper;
    private final KakaoOauthClient kakaoOauthClient;

    private final String KakaoAppId; // client-id
    private final String kakaoRedirectUri; // redirect-uri

    public OIDCDecodePayload getOIDCDecodePayload(String token) {
        OIDCPublicKeysResponse oidcPublicKeyResponse = kakaoOauthClient.getKakaoOIDCOpenKeys();

        return oauthOIDCHelper.getPayloadFromIdToken(
                token, //idToken
                "https://kauth.kakao.com",  // iss 와 대응되는 값
                KakaoAppId, // aud 와 대응되는값
                oidcPublicKeyResponse  // 공개키 목록
        );
    }

    public OauthInfo getOauthInfoByToken(String idToken) {
        OIDCDecodePayload oidcDecodePayload = getOIDCDecodePayload(idToken);
        return OauthInfo.builder()
                .socialType(SocialType.KAKAO)
                .oid(oidcDecodePayload.getSub())
                .nickname(oidcDecodePayload.getNickname())
                .profileUrl(oidcDecodePayload.getNickname())
                .gender(oidcDecodePayload.getGender())
                .ageRange(oidcDecodePayload.getAge_range())
                .email(oidcDecodePayload.getEmail())
                .build();
    }


    // ID 토큰을 가져오는 메서드 (TokenRequest 객체를 바디로 전송)
    public String getKakaoIdToken(String code) {

        // FeignClient 호출
        Map<String, Object> tokenResponse = kakaoOauthClient.getIdToken(
                "authorization_code", // grant_type
                KakaoAppId, // client_id
                kakaoRedirectUri, // redirect_uri
                code, // authorization code
                null // client_secret (필요 시 사용)
        );

        System.out.println(tokenResponse.get("id_token"));
        return (String) tokenResponse.get("id_token");
    }

}
