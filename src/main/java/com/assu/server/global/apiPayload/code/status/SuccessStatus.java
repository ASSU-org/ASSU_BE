package com.assu.server.global.apiPayload.code.status;

import com.assu.server.global.apiPayload.code.BaseCode;
import com.assu.server.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "요청 성공 및 리소스 생성됨"),

    //멤버 성공
    MEMBER_SUCCESS(HttpStatus.OK, "MEMBER_200", "성공적으로 조회되었습니다."),
    MEMBER_CREATED(HttpStatus.CREATED, "MEMBER_201", "성공적으로 생성되었습니다."),

    //인증 관련 성공
    SEND_AUTH_NUMBER_SUCCESS(HttpStatus.OK, "AUTH_200", "성공적으로 조회되었습니다."),
    VERIFY_AUTH_NUMBER_SUCCESS(HttpStatus.OK, "AUTH_201", "성공적으로 생성되었습니다."),

    //신고 성공
    REPORT_HISTORY_VIEW_SUCCESS(HttpStatus.OK, "REPORT_200", "기록 신고의 정보가 성공적으로 조회되었습니다."),
    REPORT_HISTORY_SUCCESS(HttpStatus.OK, "REPORT_201", "기록을 성공적으로 신고했습니다."),
    REPORT_COMMENT_VIEW_SUCCESS(HttpStatus.OK, "REPORT_200", "댓글 신고의 정보가 성공적으로 조회되었습니다."),
    REPORT_COMMENT_SUCCESS(HttpStatus.OK, "REPORT_201", "댓글을 성공적으로 신고했습니다."),
    REPORT_PROFILE_VIEW_SUCCESS(HttpStatus.OK, "REPORT_200", "계정 신고의 정보가 성공적으로 조회되었습니다."),
    REPORT_PROFILE_SUCCESS(HttpStatus.OK, "REPORT_201", "계정을 성공적으로 신고했습니다."),
    REPORT_ADMIN_VIEW_SUCCESS(HttpStatus.OK, "REPORT_200","관리자용 신고 기록이 성공적으로 조회되었습니다."),
    REPORT_ADMIN_PROCESSED(HttpStatus.OK,"REPORT_204","신고가 성공적으로 처리되었습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
