package com.ureca.child_recommend.user.presentation;

import com.ureca.child_recommend.contents.domain.Contents;
import com.ureca.child_recommend.global.response.SuccessResponse;
import com.ureca.child_recommend.relation.application.FeedBackService;
import com.ureca.child_recommend.user.application.UserService;
import jakarta.validation.Valid;
import com.ureca.child_recommend.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @PatchMapping("/user")
    public SuccessResponse<String> updateUserProfile(@RequestBody @Valid UserDto.Request userRequest) {
        userService.updateUserProfile(userRequest);
        return SuccessResponse.successWithoutResult(null); // 수정 완료 후 204 No Content 응답
    }



    // 좋아요한 컨텐츠 조회
    @GetMapping("/user/feedback")
    public SuccessResponse<List<String>> getLikedContents(@RequestBody Long childId) {
        List<String> likedContents = feedBackService.getLikedContents(childId);
        return SuccessResponse.success(likedContents);
    }

    @GetMapping("/user/recentcontents")
    public SuccessResponse<List<Contents>> getRecentContents(@AuthenticationPrincipal Long userId) {
        List<Contents> recentContents = feedBackService.getRecentContents(userId);
        return SuccessResponse.success(recentContents);
    }
}