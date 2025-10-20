package com.assu.server.domain.chat.dto;

import com.assu.server.domain.chat.entity.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    @JsonIgnore
    private Long roomId;
    // 메시지 삭제 시 사용 가능
    private Long messageId;

    private String message;
    private LocalDateTime sendTime;

    @JsonProperty("unreadCountForSender")
    private Integer unreadCount;

    @JsonProperty("isRead")
    private boolean isRead;

    @JsonProperty("isMyMessage")
    private boolean isMyMessage;

    private MessageType messageType;
}
