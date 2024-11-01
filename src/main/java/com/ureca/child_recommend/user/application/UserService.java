package com.ureca.child_recommend.user.application;

import com.ureca.child_recommend.config.jwt.util.JwtUtil;
import com.ureca.child_recommend.config.oauth.dto.OauthInfo;
import com.ureca.child_recommend.config.oauth.client.Helper.KakaoOauthHelper;
import com.ureca.child_recommend.config.redis.util.RedisUtil;
import com.ureca.child_recommend.global.application.S3Service;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode;
import com.ureca.child_recommend.user.domain.Users;
import com.ureca.child_recommend.user.dto.UserDto;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.JWT_REFRESHTOKEN_NOT_MATCH;
import static com.ureca.child_recommend.global.exception.errorcode.CommonErrorCode.REFRESH_TOKEN_NOT_FOUND;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KakaoOauthHelper kakaoOauthHelper;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final S3Service s3Service;
    private static final String RT = "RT:";
    private static final String LOGOUT = "LOGOUT:";
    private static final String ROLE_USER = "ROLE_USER";

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RECENT_CONTENTS_KEY_PREFIX = "recentContents:";

    public String kakaoCode(String code){
//        System.out.println(code);
        return kakaoOauthHelper.getKakaoIdToken(code);
    }

    //로그인
    @Transactional
    public UserDto.Response.SignIn login(String idToken){
        OauthInfo oauthInfo = kakaoOauthHelper.getOauthInfoByToken(idToken);

        Users user = userRepository.findByOauthInfoOid(oauthInfo.getOid()).orElseGet(()-> forceJoin(oauthInfo));

        return UserDto.Response.SignIn.of(
                jwtUtil.createAccessToken(user.getId(), ROLE_USER),
                getOrGenerateRefreshToken(user));
    }

    // 유저 정보 가져오기
    public Users getUser(String idToken) {
        OauthInfo oauthInfo = kakaoOauthHelper.getOauthInfoByToken(idToken);
        return userRepository.findByOauthInfoOid(oauthInfo.getOid()).orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));
    }

    //유저 존재하지 않을 시 생성
    public Users forceJoin(OauthInfo oauthInfo) {
        Users newUser = Users.create(oauthInfo);
        return userRepository.save(newUser);
    }

    //토큰 재발급
    public UserDto.Response.Reissue reissue(String refreshToken){
        String resolveToken = jwtUtil.resolveToken(refreshToken);
        Long userIdInToken = jwtUtil.getIdFromToken(resolveToken);

        String refreshTokenInRedis = redisUtil.getData(RT+userIdInToken);

        if(!resolveToken.equals(refreshTokenInRedis)){
            throw new BusinessException(JWT_REFRESHTOKEN_NOT_MATCH);
        }

        String newRefreshToken = jwtUtil.createRefreshToken(userIdInToken);
        String newAccessToken = jwtUtil.createAccessToken(userIdInToken, ROLE_USER);
        redisUtil.setData(RT+userIdInToken,newRefreshToken,jwtUtil.REFRESH_TOKEN_VALID_TIME);

        return UserDto.Response.Reissue.of(newRefreshToken,newAccessToken);
    }

    //로그아웃
    public void logout(String accessToken){
        String resolveToken = jwtUtil.resolveToken(accessToken);
        Long userIdInToken = jwtUtil.getIdFromToken(resolveToken);
        String refreshTokenInRedis = redisUtil.getData(RT+userIdInToken);

        if (refreshTokenInRedis == null) throw new BusinessException(REFRESH_TOKEN_NOT_FOUND);

        redisUtil.deleteData(RT+ userIdInToken);
        redisUtil.setData(LOGOUT+resolveToken, LOGOUT, jwtUtil.getExpiration(resolveToken));// 블랙리스트 처리
    }

    //토큰 얻어오기
    protected String getOrGenerateRefreshToken(Users user){
        String refreshToken = redisUtil.getData(RT + user.getId());

        if (refreshToken == null) {
            refreshToken = jwtUtil.createRefreshToken(user.getId());
            redisUtil.setData(RT + user.getId(), refreshToken, jwtUtil.REFRESH_TOKEN_VALID_TIME);
        }
        return refreshToken;
    }


    // Redis에 최근 본 컨텐츠 ID 저장 (일주일간 유지)
    public void saveRecentContent(Long childId, Long contentId) {
        String key = RECENT_CONTENTS_KEY_PREFIX + childId;
        redisTemplate.opsForList().leftPush(key, contentId);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);  // TTL 설정 (1주일)
    }

    // Redis에서 최근 본 컨텐츠 ID 목록 조회
    public List<Object> getRecentContents(Long childId) {
        String key = RECENT_CONTENTS_KEY_PREFIX + childId;
        return redisTemplate.opsForList().range(key, 0, -1);  // 전체 목록 조회
    }

    @Transactional
    public void updateUser(Long userId, UserDto.Request userRequest) {
        // 현재 사용자 정보를 가져옵니다. (예를 들어, SecurityContextHolder를 사용하여 현재 사용자 ID를 가져올 수 있습니다)
//        Long currentUserId = getCurrentUserId(); // 현재 사용자 ID를 가져오는 로직 구현 필요
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));

        // 닉네임, 이메일, 프로필 URL 업데이트
        user.updateUserInfo(userRequest.getNickname(), userRequest.getEmail(), userRequest.getGender(), userRequest.getAgeRange());
    }

    private Long getCurrentUserId() {
        // 현재 사용자의 ID를 가져오는 로직 구현
        // 예를 들어, SecurityContextHolder.getContext().getAuthentication() 사용
        return 1L; // 예시 값
    }

    @Transactional
    public void updateUserProfile(Long userId, MultipartFile image) throws IOException{
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));
         // 유저 사진 업데이트
        String profileUrl = s3Service.uploadFileImage(image, "-user");
        user.setProfileUrl(profileUrl);
    }

    public Users getUserData(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.USER_NOT_FOUND));
    }
}