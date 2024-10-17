package com.ureca.child_recommend.config.oauth;

import com.ureca.child_recommend.config.jwt.util.JwtOIDCUtil;
import com.ureca.child_recommend.config.oauth.dto.OIDCDecodePayload;
import com.ureca.child_recommend.config.oauth.dto.OIDCPublicKeyDto;
import com.ureca.child_recommend.config.oauth.dto.OIDCPublicKeysResponse;
import com.ureca.child_recommend.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.JWT_INVALID;


@Component
@RequiredArgsConstructor
public class OAuthOIDCHelper {
    private final JwtOIDCUtil jwtOIDCUtil;

    public OIDCDecodePayload getPayloadFromIdToken(
            String token, String iss, String aud, OIDCPublicKeysResponse oidcPublicKeysResponse) {
        String kid = getKidFromUnsignedIdToken(token, iss, aud);

        // KakaoOauthHelper 에서 공개키를 조회했고 해당 디티오를 넘겨준다.
        OIDCPublicKeyDto oidcPublicKeyDto =
                oidcPublicKeysResponse.getKeys().stream()
                        // 같은 kid를 가져온다.
                        .filter(o -> o.getKid().equals(kid))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(JWT_INVALID)); //예외처리 kid가 일치하지 않을때

        return jwtOIDCUtil.getOIDCTokenBody(token, oidcPublicKeyDto.getN(), oidcPublicKeyDto.getE());
    }

    // kid를 토큰에서 가져온다.
    private String getKidFromUnsignedIdToken(String token, String iss, String aud) {
        return jwtOIDCUtil.getKidFromUnsignedTokenHeader(token, iss, aud);
    }
}