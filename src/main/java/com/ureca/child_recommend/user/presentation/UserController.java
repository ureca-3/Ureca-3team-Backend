package com.ureca.child_recommend.user.presentation;

import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.relation.application.FeedBackService;
import com.ureca.child_recommend.user.application.UserService;
import com.ureca.child_recommend.user.domain.Users;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.ureca.child_recommend.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;
    private final FeedBackService feedBackService;

    /**
     * 24.10.17 작성자 : 정주현
     * 카카오 로그인
     */
    @GetMapping("/kakao-login")
    public SuccessResponse<UserDto.Response.SignIn> kakaoLogin(@RequestParam("code") final String code, HttpServletResponse servletResponse) throws IOException {
        // Step 1: code로 idToken 가져오기
        String idToken = userService.kakaoCode(code);
        // Step 2: idToken으로 로그인 처리
        UserDto.Response.SignIn response = userService.login(idToken);
        String jwtToken = response.getAccessToken();

        // 프론트에서 데이터 받기 위함 - 서버에서 테스트시 아래 두 줄 주석 처리
//        String redirectUrl = "http://localhost:3000/?token="+jwtToken; //주석

        String redirectUrl = "https://mbtiny.netlify.app/?token="+jwtToken;
        servletResponse.sendRedirect(redirectUrl);
        return SuccessResponse.success(response);
    }

    /**
     * 24.10.17 작성자 : 정주현
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public SuccessResponse<UserDto.Response.Reissue> reissue(@RequestHeader("Authorization") String refreshToken){
        UserDto.Response.Reissue response = userService.reissue(refreshToken);
        return SuccessResponse.success(response);
    }

    /**
     * 24.10.17 작성자 : 정주현
     * 로그아웃
     */
    @PostMapping("/logout")
    public SuccessResponse<String> logout(@RequestHeader("Authorization") String accessToken){
        userService.logout(accessToken);
        return SuccessResponse.success("로그아웃 성공");
    }

    @PatchMapping("/user")
    public SuccessResponse<String> updateUser(@AuthenticationPrincipal Long userId, @RequestBody @Valid UserDto.Request userRequest) {
        userService.updateUser(userId, userRequest);
        return SuccessResponse.successWithoutResult(null); // 수정 완료 후 204 No Content 응답
    }

    @PatchMapping("/user/picture")
    public SuccessResponse<String> updateUserProfile(@AuthenticationPrincipal Long userId,
                                                     @RequestPart MultipartFile profileUrl) throws IOException {
        userService.updateUserProfile(userId, profileUrl);
        return SuccessResponse.successWithoutResult(null); // 수정 완료 후 204 No Content 응답
    }

    @GetMapping("/user")
    public SuccessResponse<Users> getUserData(@AuthenticationPrincipal Long userId) {
        Users findUser = userService.getUserData(userId);
        return SuccessResponse.success(findUser);
    }
}