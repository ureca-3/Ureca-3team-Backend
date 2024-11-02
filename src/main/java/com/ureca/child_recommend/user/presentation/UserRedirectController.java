package com.ureca.child_recommend.user.presentation;

import com.ureca.child_recommend.config.oauth.OauthConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class UserRedirectController {
    private final OauthConfig oauthConfig;

    @GetMapping("/kakao")
    public void initiateKakaoLogin(HttpServletResponse response) throws IOException {
        String authUrl = "https://kauth.kakao.com/oauth/authorize?" +
                "client_id=" + oauthConfig.getKakaoClientId() +
                "&redirect_uri=" + oauthConfig.getKakaoRedirectUri() +
                "&response_type=code" ;
//                "&scope=openid,account_email,age_range,gender";

        response.sendRedirect(authUrl);
    }

    @GetMapping("/test-token")
    public ResponseEntity<Map<String, String>> getTestToken() {
        Map<String, String> response = new HashMap<>();

        // 여기서 테스트용으로 사용할 accessToken과 userId를 설정합니다.
        String testAccessToken = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImV4cCI6MTczMDk0NDEwNywiaWF0IjoxNzMwNDI1NzA3LCJpZCI6MSwicm9sZXMiOiJST0xFX1VTRVIifQ.yMNzbRm4e4hhuEPmsaaheb1ttCK3L-mbNHy7_S8oClQMrv1lDBZHLSgltYFnc0zxrucTxXjzcGUns3dJnaiLEg"; // 실제 유효한 JWT 토큰을 사용하거나 모의 토큰 사용
        String testUserId = "1"; // 실제 사용자 ID를 사용하거나 임의의 ID 사용

        response.put("accessToken", testAccessToken);
        response.put("userId", testUserId);

        return ResponseEntity.ok(response);
    }
}
