package com.ureca.child_recommend.user.application;

import com.ureca.child_recommend.config.jwt.util.JwtUtil;
import com.ureca.child_recommend.config.oauth.dto.OauthInfo;
import com.ureca.child_recommend.config.oauth.client.Helper.KakaoOauthHelper;
import com.ureca.child_recommend.config.redis.util.RedisUtil;
import com.ureca.child_recommend.global.exception.BusinessException;
import com.ureca.child_recommend.user.domain.Enum.UserRole;
import com.ureca.child_recommend.user.domain.User;
import com.ureca.child_recommend.user.dto.UserDto;
import com.ureca.child_recommend.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private static final String RT = "RT:";
    private static final String LOGOUT = "LOGOUT:";
    private static final String ROLE_USER = "ROLE_USER";

    public String kakaoCode(String code){
//        System.out.println(code);
        return kakaoOauthHelper.getKakaoIdToken(code);
    }

    //로그인
    @Transactional
    public UserDto.Response.SignIn login(String idToken){
        OauthInfo oauthInfo = kakaoOauthHelper.getOauthInfoByToken(idToken);

        User user = userRepository.findByOauthInfoOid(oauthInfo.getOid()).orElseGet(()-> forceJoin(oauthInfo));

        return UserDto.Response.SignIn.of(
                jwtUtil.createAccessToken(user.getId(), ROLE_USER),
                getOrGenerateRefreshToken(user));
    }

    //유저 존재하지 않을 시 생성
    public User forceJoin(OauthInfo oauthInfo) {
        User newUser = User.create(oauthInfo);
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

        String newRefreshToken =jwtUtil.createRefreshToken(userIdInToken);
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

        redisUtil.deleteDate(RT+ userIdInToken);
        redisUtil.setData(LOGOUT+resolveToken, LOGOUT, jwtUtil.getExpiration(resolveToken));// 블랙리스트 처리
    }

//
    //토큰 얻어오기
    protected String getOrGenerateRefreshToken(User user){
        String refreshToken = redisUtil.getData(RT + user.getId());

        if (refreshToken == null) {
            refreshToken = jwtUtil.createRefreshToken(user.getId());
            redisUtil.setData(RT + user.getId(), refreshToken, jwtUtil.REFRESH_TOKEN_VALID_TIME);
        }
        return refreshToken;
    }

    public void updateNickname(Long userid, Long userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        User User = userRepository.findById(userid)
                .orElseThrow(() -> new IllegalArgumentException("본인 정보가 존재하지 않습니다."));

        if(userid.equals(userId) || User.getRole() == UserRole.GUEST) {user.updateNickname(newNickname);}
        userRepository.save(user);
    }

    public void updatePhone(Long userid, Long userId,  String newPhone) {//본인id, 업데이트하고자하는id,수정값
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        User User = userRepository.findById(userid)
                .orElseThrow(() -> new IllegalArgumentException("본인 계정 정보가 존재하지 않습니다."));
        if(userid.equals(userId) || User.getRole() == UserRole.GUEST) {user.updatePhone(newPhone);}
        userRepository.save(user);
    }

    public void deleteUser(Long userid, Long userId) {
        User User = userRepository.findById(userid)
                .orElseThrow(() -> new IllegalArgumentException("본인 계정 정보가 존재하지 않습니다."));
        if(userid.equals(userId) || User.getRole() == UserRole.GUEST) {userRepository.deleteById(userId);}
    }
}

