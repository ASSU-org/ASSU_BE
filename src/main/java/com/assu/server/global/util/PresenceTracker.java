package com.assu.server.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class PresenceTracker {
    private final Map<Long, Set<Long>> roomSubscribers = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionToMember = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> sessionToRooms = new ConcurrentHashMap<>();

    private Long parseRoomId(String dest) { // "/sub/chat/26" -> 26
        if (dest == null) return null;
        String[] p = dest.split("/");
        if (p.length >= 4 && "chat".equals(p[2])) return Long.valueOf(p[3]);
        return null;
    }

    private Long memberIdFrom(Principal user) {
        if (user == null) return null;
        // StompAuthChannelInterceptor 에서 Principal.name을 memberId로 넣어두었다고 가정
        return Long.valueOf(user.getName());
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        Long roomId = parseRoomId(acc.getDestination());
        Long memberId = memberIdFrom(e.getUser());
        if (roomId == null || memberId == null) return;

        String sessionId = acc.getSessionId();
        sessionToMember.put(sessionId, memberId);
        sessionToRooms.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(roomId);
        roomSubscribers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(memberId);

        log.debug("SUB: member {} -> room {}", memberId, roomId);
    }

    @EventListener
    public void onUnsubscribe(SessionUnsubscribeEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        String sessionId = acc.getSessionId();
        var rooms = sessionToRooms.getOrDefault(sessionId, Set.of());
        Long memberId = sessionToMember.get(sessionId);
        if (memberId != null) {
            for (Long roomId : rooms) {
                var set = roomSubscribers.get(roomId);
                if (set != null) {
                    set.remove(memberId);
                    if (set.isEmpty()) roomSubscribers.remove(roomId);
                }
            }
        }
        sessionToRooms.remove(sessionId);
        log.debug("UNSUB: session {}", sessionId);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        String sessionId = e.getSessionId();
        Long memberId = sessionToMember.remove(sessionId);
        var rooms = sessionToRooms.remove(sessionId);
        if (memberId != null && rooms != null) {
            for (Long roomId : rooms) {
                var set = roomSubscribers.get(roomId);
                if (set != null) {
                    set.remove(memberId);
                    if (set.isEmpty()) roomSubscribers.remove(roomId);
                }
            }
        }
        log.debug("DISCONNECT: session {}", sessionId);
    }

    public boolean isInRoom(Long memberId, Long roomId) {
        return roomSubscribers.getOrDefault(roomId, Set.of()).contains(memberId);
    }
}
