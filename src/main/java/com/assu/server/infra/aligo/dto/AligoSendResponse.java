package com.assu.server.infra.aligo.dto;

import lombok.Data;

@Data
public class AligoSendResponse {
    private String result_code; // 성공 여부
    private String message;     // 결과 메시지
    private String msg_id;      // 메시지 ID
}
