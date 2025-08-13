package com.assu.server.global.apiPayload.code.status;

import com.assu.server.global.apiPayload.code.BaseErrorCode;
import com.assu.server.global.apiPayload.code.ErrorReasonDTO;
import com.sun.net.httpserver.HttpsServer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
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
    NO_SUCH_PARTNER(HttpStatus.NOT_FOUND,"MEMBER_4002","존재하지 않는 partner ID 입니다."),

    // 채팅 에러
    NO_SUCH_ROOM(HttpStatus.NOT_FOUND, "CHATTING_5001", "존재하지 않는 채팅방 ID 입니다."),
    NO_MEMBER_IN_THE_ROOM(HttpStatus.NOT_FOUND, "CHATTING_5002", "해당 방에는 해당 사용자가 없습니다."),
    NO_MEMBER(HttpStatus.NOT_FOUND, "CHATTING_5003", "해당 방에는 사용자가 아무도 없습니다."),
    NO_MESSAGE(HttpStatus.NOT_FOUND, "CHATTING_5004", "해당 방에는 메시지가 아무것 없습니다."),


    // 어드민 에러
    NO_SUCH_ADMIN(HttpStatus.NOT_FOUND, "ADMIN_5001", "존재하지 않는 학생회입니다."),

    // 파트너 에러
    NO_SUCH_PARTNER(HttpStatus.NOT_FOUND, "PARTNER_5003", "존재하지 않는 파트너입니다."),

    // 학생 에러
    NO_SUCH_STUDENT(HttpStatus.NOT_FOUND, "STUDENT_5004", "존재하지 않는 학생입니다."),

    // 스토어 에러
    NO_SUCH_STORE(HttpStatus.NOT_FOUND, "STORE_6001", "존재하지 않는 가게입니다."),

    // 혜택 없음 에러
    OPTION_NOT_EMPTY(HttpStatus.BAD_REQUEST, "OPTION_7001", "혜택은 한 가지 이상이어야 합니다."),

    // 벨류(금액, 인원수) 에러
    VALUE_IS_REQUIRED(HttpStatus.NOT_FOUND, "VALUE_8001", "값을 알 수 없습니다."),

    // 서비스 아이템 에러
    SERVICE_ITEM_REQUIRED(HttpStatus.NOT_FOUND, "SERVICE_ITEM_9001", "서비스 품목은 한 가지 이상이어야 합니다."),

    // 카테고리 에러
    CATEGORY_REQUIRED(HttpStatus.NOT_FOUND, "CATEGORY_10001", "품목에 대한 카테고리가 설정되어야 합니다."),

    // 할인율 에러
    DISCOUNT_RATE_REQUIRED(HttpStatus.NOT_FOUND, "DISCOUNT_11001", "할인율 값을 알 수 없습니다."),

    // 혜택 타입 에러
    UNSUPPORTED_OPTION_TYPE(HttpStatus.NOT_FOUND, "OPTION_7002", "지원하지 않는 혜택 항목입니다."),

    // 제휴 아이디 에러
    NO_SUCH_PAPER(HttpStatus.NOT_FOUND, "PAPER_12001", "존재하지 않는 제휴입니다."),

    // 어드민 찾기 에러
    NO_AVAILABLE_ADMIN(HttpStatus.NOT_FOUND, "ADMIN_5002", "제휴단체를 찾을 수 없습니다."),

    // 파트너 찾기 에러
    NO_AVAILABLE_PARTNER(HttpStatus.NOT_FOUND, "PARTNER_5502", "제휴업체를 찾을 수 없습니다."),

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
