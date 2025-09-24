package com.assu.server.domain.chat.repository;

import com.assu.server.domain.chat.dto.ChatMessageDTO;
import com.assu.server.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
        SELECT m FROM Message m
        WHERE m.chattingRoom.id = :roomId
        AND m.receiver.id = :receiverId
        AND m.isRead = false
""")
    List<Message> findUnreadMessagesByRoomAndReceiver(Long roomId, Long receiverId);

    @Query("""
        SELECT COUNT(m)
        FROM Message m
        WHERE m.chattingRoom.id = :roomId
        AND m.receiver.id = :receiverId
        AND m.isRead = false
    """)
    Long countUnreadMessagesByRoomAndReceiver(@Param("roomId") Long roomId, @Param("receiverId") Long receiverId);


    @Query("""
        SELECT new com.assu.server.domain.chat.dto.ChatMessageDTO (
            m.chattingRoom.id,
            m.id,
            m.message,
            m.createdAt,
            m.unreadCount,
            m.isRead,
            CASE WHEN m.sender.id = :memberId THEN true
            ELSE false
            END
        )
        FROM Message m
        WHERE m.chattingRoom.id = :roomId
        AND (m.sender.id = :memberId OR m.receiver.id = :memberId)
                ORDER BY m.createdAt ASC
""")
    List<ChatMessageDTO> findAllMessagesByRoomAndMemberId(
            @Param("roomId") Long roomId,
            @Param("memberId") Long memberId
    );

}
