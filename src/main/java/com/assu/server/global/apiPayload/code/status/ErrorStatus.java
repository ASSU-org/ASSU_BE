package com.assu.server.global.apiPayload.code.status;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import com.assu.server.global.apiPayload.code.ErrorReasonDTO;
import com.sun.net.httpserver.HttpsServer;
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
    PAGE_SIZE_INVALID(HttpStatus.BAD_REQUEST,"PAGE_4002","size는 1~200 사이여야 합니다."),

    // 멤버 에러
    NO_SUCH_MEMBER(HttpStatus.NOT_FOUND,"MEMBER_4001","존재하지 않는 멤버 ID입니다."),
    NO_SUCH_ADMIN(HttpStatus.NOT_FOUND,"MEMBER_4002","존재하지 않는 admin ID 입니다."),
    NO_SUCH_PARTNER(HttpStatus.NOT_FOUND,"MEMBER_4002","존재하지 않는 partner ID 입니다."),

    // 채팅 에러
    NO_SUCH_ROOM(HttpStatus.NOT_FOUND, "CHATTING_5001", "존재하지 않는 채팅방 ID 입니다."),
    NO_MEMBER_IN_THE_ROOM(HttpStatus.NOT_FOUND, "CHATTING_5002", "해당 방에는 해당 사용자가 없습니다."),
    NO_MEMBER(HttpStatus.NOT_FOUND, "CHATTING_5003", "해당 방에는 사용자가 아무도 없습니다."),
    NO_MESSAGE(HttpStatus.NOT_FOUND, "CHATTING_5004", "해당 방에는 메시지가 아무것 없습니다."),

    // 알림(Notification) 에러
    INVALID_NOTIFICATION_STATUS_FILTER(HttpStatus.BAD_REQUEST,"NOTIFICATION_4001","유효하지 않은 알림 status 필터입니다. (all | unread 만 허용)"),
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST,"NOTIFICATION_4002","지원하지 않는 알림 타입입니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND,"NOTIFICATION_4003","존재하지 않는 알림입니다."),
    NOTIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN,"NOTIFICATION_4004","해당 알림에 접근할 권한이 없습니다."),
    MISSING_NOTIFICATION_FIELD(HttpStatus.BAD_REQUEST,"NOTIFICATION_4005","알림 생성에 필요한 필드가 누락되었습니다."),

    // 디바이스 토큰(DeviceToken) 에러
    DEVICE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"DEVICE_4001","존재하지 않는 Device Token 입니다."),
    DEVICE_TOKEN_REGISTER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"DEVICE_5001","Device Token 등록에 실패했습니다.")

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
