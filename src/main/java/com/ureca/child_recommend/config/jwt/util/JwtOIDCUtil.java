package com.ureca.child_recommend.config.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ureca.child_recommend.config.oauth.dto.OIDCDecodePayload;
import com.ureca.child_recommend.global.exception.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.*;


@Service
@Getter
@Slf4j
public class JwtOIDCUtil {
    private final String KID = "kid";

    /**
     * 미인증 토큰에서 Kid를 가져옴
     */
    public String getKidFromUnsignedTokenHeader(String token, String iss, String aud) {
        DecodedJWT decodedJWT = getUnsignedTokenClaims(token, iss, aud);
        return decodedJWT.getHeaderClaim(KID).asString();
    }

    /**
     * Header, Payload, Signature 를 분리하고 검증한다.
     */
    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");
        if (splitToken.length != 3) throw new BusinessException(JWT_BAD);
        return splitToken[0] + "." + splitToken[1] + ".";
    }


    /**
     *  iss와 aud의 검증
     */
    private DecodedJWT getUnsignedTokenClaims(String token, String iss, String aud) {
        try {
            DecodedJWT decodedJWT = JWT.decode(getUnsignedToken(token));
            if (!decodedJWT.getAudience().contains(aud)) {
                throw new BusinessException(JWT_INVALID);
            }
            if (!decodedJWT.getIssuer().equals(iss)) {
                throw new BusinessException(JWT_INVALID);
            }
            return decodedJWT;
        } catch (JWTDecodeException e) {
            throw new BusinessException(JWT_BAD);
        }
    }
    /**
     *  JWT 디코딩후 OIDC Body에 들어있는 정보를 가져오는 메소드
     */
    public OIDCDecodePayload getOIDCTokenBody(String token, String modulus, String exponent) {
        DecodedJWT decodedJWT = getOIDCTokenJws(token, modulus, exponent);
        return new OIDCDecodePayload(
                decodedJWT.getIssuer(),
                decodedJWT.getAudience().get(0),
                decodedJWT.getSubject(),
                decodedJWT.getClaim("profile_nickname").asString(),
                decodedJWT.getClaim("profile_image").asString(),
                decodedJWT.getClaim("account_email").asString(),
                decodedJWT.getClaim("gender").asString(), // age_range 추가
                decodedJWT.getClaim("age_range").asString());    // gender 추가
    }

    /**
     * 서명 검증을 포함하여 JWT를 검증
     */

    public DecodedJWT getOIDCTokenJws(String token, String modulus, String exponent) {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) getRSAPublicKey(modulus, exponent), null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new BusinessException(JWT_EXPIRED);
        } catch (Exception e) {
            throw new BusinessException(JWT_BAD);
        }
    }

    /**
     * Base64로 인코딩된 RSA 공개 키 모듈러스와 지수를 사용하여 RSAPublicKey 객체를 생성
     */
    private Key getRSAPublicKey(String modulus, String exponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);
        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
        return keyFactory.generatePublic(keySpec);
    }
}
