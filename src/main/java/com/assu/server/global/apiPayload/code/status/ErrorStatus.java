package com.assu.server.global.apiPayload.code.status;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import com.assu.server.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 기본 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //페이징 에러
    PAGE_UNDER_ONE(HttpStatus.BAD_REQUEST,"PAGE_4001","페이지는 1이상이여야 합니다."),

    // 멤버 에러
    NO_SUCH_MEMBER(HttpStatus.NOT_FOUND,"MEMBER_4001","존재하지 않는 멤버 ID입니다."),
    NO_SUCH_ADMIN(HttpStatus.NOT_FOUND,"MEMBER_4002","존재하지 않는 admin ID 입니다."),
    NO_SUCH_PARTNER(HttpStatus.NOT_FOUND,"MEMBER_4002","존재하지 않는 partner ID 입니다.")

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
