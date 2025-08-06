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

    //옷 성공
    CLOTH_SUCCESS(HttpStatus.OK, "CLOTH_200", "옷이 성공적으로 조회되었습니다."),
    CLOTH_CREATED(HttpStatus.CREATED, "CLOTH_201", "옷이 성공적으로 생성되었습니다."),
    CLOTH_EDITED(HttpStatus.NO_CONTENT, "CLOTH_204", "옷이 성공적으로 수정되었습니다."),
    CLOTH_DELETED(HttpStatus.NO_CONTENT, "CLOTH_204", "옷이 성공적으로 삭제되었습니다."),

    //카테고리 성공
    CATEGORY_SUCCESS(HttpStatus.OK, "CATEGORY_200", "성공적으로 조회되었습니다."),
    CATEGORY_CREATED(HttpStatus.CREATED, "CATEGORY_201", "성공적으로 생성되었습니다."),

    //폴더 성공
    FOLDER_SUCCESS(HttpStatus.OK, "FOLDER_200", "성공적으로 조회되었습니다."),
    FOLDER_CREATED(HttpStatus.CREATED, "FOLDER_201", "성공적으로 생성되었습니다."),
    FOLDER_DELETED(HttpStatus.NO_CONTENT, "FOLDER_204", "성공적으로 삭제되었습니다."),
    FOLDER_EDIT_SUCCESS(HttpStatus.NO_CONTENT, "FOLDER_204", "성공적으로 수정되었습니다."),
    FOLDER_ADD_CLOTHES_SUCCESS(HttpStatus.CREATED, "FOLDER_201", "성공적으로 추가되었습니다."),
    FOLDER_DELETE_CLOTHES_SUCCESS(HttpStatus.NO_CONTENT, "FOLDER_204", "성공적으로 삭제되었습니다."),
    FOLDER_CLOTHES_SUCCESS(HttpStatus.OK, "FOLDER_200", "성공적으로 반영되었습니다."),

    //검색 성공
    SEARCH_SUCCESS(HttpStatus.OK, "SEARCH_200", "성공적으로 조회되었습니다."),

    //기록 성공
    HISTORY_SUCCESS(HttpStatus.OK, "HISTORY_200", "성공적으로 조회되었습니다."),
    HISTORY_CREATED(HttpStatus.CREATED, "HISTORY_201", "성공적으로 생성되었습니다."),
    HISTORY_LIKE_STATUS_CHANGED(HttpStatus.OK,"HISTORY_200","좋아요 상태가 성공적으로 변경되었습니다."),
    HISTORY_COMMENT_CREATED(HttpStatus.CREATED,"HISTORY_201","성공적으로 댓글이 생성되었습니다."),
    HISTORY_UPDATED(HttpStatus.NO_CONTENT,"HISTORY_204","성공적으로 수정되었습니다"),
    HISTORY_COMMENT_DELETED(HttpStatus.NO_CONTENT,"HISTORY_204","댓글이 성공적으로 삭제되었습니다"),
    HISTORY_COMMENT_UPDATED(HttpStatus.NO_CONTENT,"HISTORY_204","댓글이 성공적으로 수정되었습니다"),
    HISTORY_DELETED(HttpStatus.NO_CONTENT,"HISTORY_204","기록이 성공적으로 삭제되었습니다"),
    HISTORY_LIKE_USER(HttpStatus.OK,"HISTORY_200","기록의 좋아요를 누른 유저 정보를 성공적으로 조회했습니다."),
    HISTORY_CHECK_SUCCESS(HttpStatus.OK, "HISTORY_200","나의 기록인지 성공적으로 조회했습니다."),

    //알림 성공
    NOTIFICATION_SUCCESS(HttpStatus.OK, "NOTIFICATION_200", "성공적으로 조회되었습니다."),
    UNREAD_NOTIFICATION_CHECKED(HttpStatus.OK,"NOTIFICATION_200","읽지 않은 알림 여부가 성공적으로 조회되었습니다."),
    NOTIFICATION_READ(HttpStatus.NO_CONTENT,"NOTIFICATION_204","알림이 성공적으로 읽음 처리되었습니다."),
    NOTIFICATION_SEND_SUCCESS(HttpStatus.NO_CONTENT,"NOTIFICATION_204","알림이 성공적으로 발송되었습니다"),
    NOTIFICATION_HISTORY_LIKED_SUCCESS(HttpStatus.OK,"NOTIFICATION_200","기록 좋아요 알림이 성공적으로 발송되었습니다."),
    NOTIFICATION_NEW_FOLLOWER_SUCCESS(HttpStatus.OK,"NOTIFICATION_200","팔로우 알림이 성공적으로 발송되었습니다."),
    NOTIFICATION_HISTORY_COMMENT_SUCCESS(HttpStatus.OK,"NOTIFICATION_200","기록 댓글 알림이 성공적으로 발송되었습니다."),
    NOTIFICATION_REPLY_SUCCESS(HttpStatus.OK,"NOTIFICATION_200","댓글에 대한 답글 알림이 성공적으로 발송되었습니다."),

    //홈 성공
    HOME_SUCCESS(HttpStatus.OK, "HOME_200", "성공적으로 조회되었습니다."),

    //기타 멤버 관련 성공
    MEMBER_ACTION_SUCCESS(HttpStatus.OK, "MEMBER_ACTION_200", "멤버 관련 요소가 성공적으로 조회되었습니다."),
    MEMBER_ACTION_CREATED(HttpStatus.CREATED, "MEMBER_ACTION_201", "멤버 관련 요소가 성공적으로 생성되었습니다."),
    MEMBER_ACTION_EDITED(HttpStatus.OK, "MEMBER_ACTION_204", "멤버 관련 요소가 성공적으로 수정되었습니다."),


    //아이디 성공
    MEMBER_ID_SUCCESS(HttpStatus.OK, "MEMBER_ID_200", "사용가능한 아이디입니다."),

    //로그인 성공
    LOGIN_SUCCESS(HttpStatus.OK, "LOGIN_200", "로그인에 성공하였습니다."),
    LOGIN_CREATED(HttpStatus.CREATED, "LOGIN_201", "회원가입과 로그인에 성공하였습니다."),
    LOGIN_UPDATED(HttpStatus.NO_CONTENT, "LOGIN_204", "로그인 정보가 성공적으로 수정되었습니다."),

    //로그아웃 성공
    LOGOUT_SUCCESS(HttpStatus.OK, "LOGOUT_200", "로그아웃에 성공하였습니다."),
    UNLINK_SUCCESS(HttpStatus.OK, "UNLINK_200", "회원탈퇴에 성공하였습니다."),

    //Elastic Search 인덱스 생성 및 동기화 성공
    CLOTH_SYNC_CREATED(HttpStatus.CREATED, "SEARCH_201", "옷 검색 인덱스가 성공적으로 생성되었습니다."),
    HISTORY_SYNC_CREATED(HttpStatus.CREATED, "SEARCH_201", "기록 검색 인덱스가 성공적으로 생성되었습니다."),
    MEMBER_SYNC_CREATED(HttpStatus.CREATED, "SEARCH_201", "유저 검색 인덱스가 성공적으로 생성되었습니다."),


    //신고 성공
    REPORT_HISTORY_VIEW_SUCCESS(HttpStatus.OK, "REPORT_200", "기록 신고의 정보가 성공적으로 조회되었습니다."),
    REPORT_HISTORY_SUCCESS(HttpStatus.OK, "REPORT_201", "기록을 성공적으로 신고했습니다."),
    REPORT_COMMENT_VIEW_SUCCESS(HttpStatus.OK, "REPORT_200", "댓글 신고의 정보가 성공적으로 조회되었습니다."),
    REPORT_COMMENT_SUCCESS(HttpStatus.OK, "REPORT_201", "댓글을 성공적으로 신고했습니다."),
    REPORT_PROFILE_VIEW_SUCCESS(HttpStatus.OK, "REPORT_200", "계정 신고의 정보가 성공적으로 조회되었습니다."),
    REPORT_PROFILE_SUCCESS(HttpStatus.OK, "REPORT_201", "계정을 성공적으로 신고했습니다."),
    REPORT_ADMIN_VIEW_SUCCESS(HttpStatus.OK, "REPORT_200","관리자용 신고 기록이 성공적으로 조회되었습니다."),
    REPORT_ADMIN_PROCESSED(HttpStatus.OK,"REPORT_204","신고가 성공적으로 처리되었습니다."),

    // 제휴 성공
    PAPER_HISTORY_SUCCESS(HttpStatus.OK, "PAPER201", "가게 별 제휴 내용이 성공적으로 조회되었습니다."),


    // 그룹 인증
    GROUP_SESSION_CREATE(HttpStatus.OK, "GROUP201", "인증 세션 생성 및 대표자 구독이 완료되었습니다."),
    GROUP_CERTIFICATION_SUCCESS(HttpStatus.OK, "GROUP202", "그룹 인증 세션에 대한 인증이 완료되었습니다.")
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
