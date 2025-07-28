package com.assu.server.domain.chat.repository;

import com.assu.server.domain.chat.dto.ChatRoomListResultDTO;
import com.assu.server.domain.chat.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChattingRoom, Long> {

    @Query("""
    SELECT new com.assu.server.domain.chat.dto.ChatRoomListResultDTO (
    r.id,
    (SELECT m.message
     FROM Message m
     WHERE m.chattingRoom.id = r.id
     AND m.sendTime = (
         SELECT MAX(m2.sendTime)
         FROM Message m2
         WHERE m2.chattingRoom.id = r.id
     )
    ),
    (SELECT MAX(m.sendTime)
     FROM Message m
     WHERE m.chattingRoom.id = r.id
    ),
    (SELECT COUNT(m)
     FROM Message m
     WHERE m.chattingRoom.id = r.id
     AND m.receiver.id = :memberId
     AND m.isRead = false),
    CASE WHEN r.partner.member.id = :memberId THEN r.admin.member.id ELSE r.partner.member.id END,
    CASE WHEN r.partner.member.id = :memberId THEN r.admin.name ELSE r.partner.name END,
    CASE WHEN r.partner.member.id = :memberId THEN r.admin.member.profileUrl ELSE r.partner.member.profileUrl END
    )
    FROM ChattingRoom r
    WHERE r.partner.member.id = :memberId OR r.admin.member.id = :memberId
    """)
    List<ChatRoomListResultDTO> findChattingRoomByMember(@Param("memberId") Long memberId);
}
