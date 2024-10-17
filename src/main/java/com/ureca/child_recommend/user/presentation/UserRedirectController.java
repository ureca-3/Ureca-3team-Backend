package com.ureca.child_recommend.user.presentation;

import com.ureca.child_recommend.config.oauth.OauthConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
                "&response_type=code" +
                "&scope=openid account_email,age_range,gender"; // 추가 동의 항목 포함

        log.info("Redirecting to Kakao OAuth2 URL: {}", authUrl);
        response.sendRedirect(authUrl);
    }
}
