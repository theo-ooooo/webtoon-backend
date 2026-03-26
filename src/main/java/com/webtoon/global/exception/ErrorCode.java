package com.webtoon.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C003", "접근 권한이 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C004", "인증이 필요합니다."),

    // Auth
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "A001", "이미 사용 중인 이메일입니다."),
    NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "A002", "이미 사용 중인 닉네임입니다."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "A003", "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "A004", "사용자를 찾을 수 없습니다."),

    // Comic
    COMIC_NOT_FOUND(HttpStatus.NOT_FOUND, "CM001", "만화를 찾을 수 없습니다."),

    // Episode
    EPISODE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "에피소드를 찾을 수 없습니다."),
    EPISODE_NOT_PURCHASED(HttpStatus.FORBIDDEN, "E002", "유료 에피소드입니다. 구매 후 열람할 수 있습니다."),

    // Purchase
    ALREADY_PURCHASED(HttpStatus.CONFLICT, "P001", "이미 구매한 에피소드입니다."),
    FREE_EPISODE(HttpStatus.BAD_REQUEST, "P002", "무료 에피소드는 구매할 필요가 없습니다."),

    // Coin
    INSUFFICIENT_COIN(HttpStatus.BAD_REQUEST, "CO001", "코인이 부족합니다."),

    // Rating
    INVALID_SCORE(HttpStatus.BAD_REQUEST, "R001", "별점은 1~5 사이여야 합니다."),

    // Notice
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "공지사항을 찾을 수 없습니다."),

    // Upload
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "U001", "파일 업로드에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
