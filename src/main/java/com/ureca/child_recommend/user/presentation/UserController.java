package com.ureca.child_recommend.user.presentation;

import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.user.application.UserService;
import com.ureca.child_recommend.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    /**
     * 24.10.17 작성자 : 정주현
     * 카카오 로그인
     */
    @GetMapping("/kakao-login")
    public SuccessResponse<UserDto.Response.SignIn> kakaoLogin(@RequestParam final String code){
        // Step 1: code로 idToken 가져오기
        String idToken = userService.kakaoCode(code);
        // Step 2: idToken으로 로그인 처리
        UserDto.Response.SignIn response = userService.login(idToken);

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

    @PatchMapping("/user/nickname")
    public SuccessResponse<String> updateNickname(@AuthenticationPrincipal Long userId , @RequestBody String newNickname) {
        userService.updateNickname(userId, newNickname);
        return SuccessResponse.successWithoutResult(null);
    }

    // 전화번호 수정
    @PatchMapping("/user/phone")
    public SuccessResponse<String> updatePhone(@AuthenticationPrincipal Long userId , @RequestBody String newPhone) {
        userService.updatePhone(userId, newPhone);
        return SuccessResponse.successWithoutResult(null);
    }

    // 유저 정지
    @PatchMapping("/user")
    public SuccessResponse<String> deleteUser(@AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
        return SuccessResponse.successWithoutResult(null);
    }
}