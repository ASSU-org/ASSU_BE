package com.assu.server.domain.chat.dto;

public record MessageHandlingResult(
        ChatResponseDTO.SendMessageResponseDTO sendMessageResponseDTO,
        ChatRoomUpdateDTO chatRoomUpdateDTO,
        Long receiverId
) {

    // 정적 팩토리 메소드 1
    public static MessageHandlingResult of(ChatResponseDTO.SendMessageResponseDTO sendMessageDTO) {
        // record의 기본 생성자를 호출합니다.
        return new MessageHandlingResult(sendMessageDTO, null, null);
    }

    // 정적 팩토리 메소드 2
    public static MessageHandlingResult withUpdates(ChatResponseDTO.SendMessageResponseDTO sendMessageDTO, ChatRoomUpdateDTO updateDTO, Long receiverId) {
        // record의 기본 생성자를 호출합니다.
        return new MessageHandlingResult(sendMessageDTO, updateDTO, receiverId);
    }

    // 헬퍼(Helper) 메소드
    public boolean hasRoomUpdates() {
        // record는 'get' 접두사 없는 접근자(chatRoomUpdateDTO())를 사용합니다.
        return chatRoomUpdateDTO != null;
    }
}