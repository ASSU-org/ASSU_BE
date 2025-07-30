package com.assu.server.domain.chat.repository;

import com.assu.server.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
        SELECT m FROM Message m
        WHERE m.chattingRoom.id = :roomId
        AND m.receiver.id = :receiverId
        AND m.isRead = false
""")
    List<Message> findUnreadMessagesByRoomAndReceiver(Long roomId, Long receiverId);
}
