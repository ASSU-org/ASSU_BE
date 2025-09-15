package com.assu.server.infra.aligo.dto;

import lombok.Data;

@Data
public class AligoSendResponse {
    private String result_code; // 성공 여부
    private String message;     // 결과 메시지
    private String msg_id;      // 메시지 ID
    private String success_cnt; // 성공 개수
    private String error_cnt;   // 에러 개수
    private String msg_type;    // 메시지 타입
}
