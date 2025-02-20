package com.ureca.child_recommend.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{
    // 공용 처리
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "4000", "Invalid parameter included"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "4040", "Resource not exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000", "알수없는 에러 관리자에게 문의"),

    // 인증/인가 처리 (41xx)
    JWT_EMPTY(HttpStatus.UNAUTHORIZED,"4100","JWT 토큰을 넣어주세요."),
    JWT_INVALID(HttpStatus.BAD_REQUEST,"4101","다시 로그인 해주세요.(토큰이 유효하지 않습니다.)"),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED,"4102","토큰이 만료되었습니다."),
    JWT_BAD(HttpStatus.BAD_REQUEST,"4103","JWT 토큰이 잘못되었습니다."),
    JWT_REFRESHTOKEN_NOT_MATCH(HttpStatus.CONFLICT,"4104","RefreshToken이 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "4105", "리프레시 토큰을 찾을 수 없습니다."),
    JWT_AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED,"4106","권한이 없습니다."),

    //user error (4001~
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"4001","해당 유저를 찾을 수 없습니다."),
    LOGOUT_MEMBER(HttpStatus.FORBIDDEN, "3001", "로그아웃된 사용자입니다.(재 로그인 하세요."),

    TEST_NOT_FOUND(HttpStatus.UNAUTHORIZED,"8888","테스트 아이디가 없습니다."),

    // child error (4201~
    CHILD_NOT_FOUND(HttpStatus.NOT_FOUND,"4201","해당 자녀를 찾을 수 없습니다."),
    CHILDMBTI_NOT_FOUND(HttpStatus.NOT_FOUND,"4202","해당 자녀MBTI 정보를 찾을 수 없습니다."),


    // mbti 진단 error (4301~
    ASSESSMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"4301","해당 진단 내역을 찾을 수 없습니다."),

    // contents error (4401 ~
    CONTENTS_NOT_FOUND(HttpStatus.NOT_FOUND, "4401", "해당 콘텐츠를 찾을 수 없습니다."),

    // gpt error (4501 ~
    GPT_SERVER_ERROR(HttpStatus.BAD_REQUEST, "4501", "GPT를 연결할 수 없습니다."),

    // 추가할 NO_CONTENT 관련 에러 코드
    NO_CONTENT_AVAILABLE(HttpStatus.NO_CONTENT, "2040", "요청한 리소스가 없습니다."),
    // history error (4601 ~
    HISTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "4601", "히스토리 내역이 없습니다."),

    //FeedBack error(4701~
    FEEDBACK_ALREADY_DISLIKE(HttpStatus.BAD_REQUEST, "4701", "이미 이 콘텐츠에 싫어요를 누르셨습니다. 중복 피드백은 허용되지 않습니다."),
    FEEDBACK_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "4702", "이미 이 콘텐츠에 좋아요를 누르셨습니다. 중복 피드백은 허용되지 않습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
