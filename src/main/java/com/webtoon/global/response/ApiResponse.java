package com.webtoon.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.webtoon.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int status;
    private T data;
    private ErrorDetail error;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, data, null);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(200, null, null);
    }

    public static ApiResponse<Void> created() {
        return new ApiResponse<>(201, null, null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, data, null);
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getStatus().value(), null,
                new ErrorDetail(errorCode.getCode(), errorCode.getMessage()));
    }

    public static ApiResponse<Void> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.getStatus().value(), null,
                new ErrorDetail(errorCode.getCode(), message));
    }

    public static ApiResponse<Object> error(ErrorCode errorCode, Object details) {
        return new ApiResponse<>(errorCode.getStatus().value(), null,
                new ErrorDetail(errorCode.getCode(), errorCode.getMessage(), details));
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {
        private String code;
        private String message;
        private Object details;

        public ErrorDetail(String code, String message) {
            this(code, message, null);
        }
    }
}
