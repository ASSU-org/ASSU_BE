package com.assu.server.infra;

import com.assu.server.domain.auth.entity.Member;
import com.assu.server.domain.notification.entity.Notification;
import com.assu.server.domain.notification.entity.NotificationType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationFactory {

    public Notification create(Member receiver, NotificationType type, Long refId, Map<String, Object> ctx) {
        String title;
        String preview;
        String deeplink;

        switch (type) {
            case CHAT -> {
                String sender = asString(ctx.get("senderName"), "알 수 없음");
                String msg = asString(ctx.get("message"), "");
                title = "ASSU";
                preview = sender + ": " + truncateByCodePoint(msg, 200);
                deeplink = "/chat/rooms/" + refId;
            }
            case PARTNER_SUGGESTION -> {
                title = "제휴 건의";
                preview = "새로운 제휴 건의가 도착했어요!";
                deeplink = "/partner/suggestions/" + refId;
            }
            case ORDER -> {
                title = "주문 안내";
                String tableNum = asString(ctx.get("table_num"), "?");
                String paper = asString(ctx.get("paper_content"), "선택한 혜택");
                preview = tableNum + "번 테이블에서 " + paper + " 혜택을 선택하셨어요.";
                deeplink = "/orders/" + refId; // 애매함, UI 상에서 이동할 곳이 없음
            }
            case PARTNER_PROPOSAL -> {
                String partnerName = asString(ctx.get("partner_name"), "파트너");
                title = "제휴 제안";
                preview = partnerName + "에서 제휴 제안이 왔어요!";
                deeplink = "/partner/proposals/" + refId;
            }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }

        return Notification.builder()
                .receiver(receiver)
                .type(type)
                .refId(refId)
                .title(title)
                .messagePreview(preview)
                .deeplink(deeplink)
                .isRead(false)
                .build();
    }

    // ===== helpers =====
    private static String asString(Object v, String def) {
        if (v == null) return def;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? def : s;
    }

    /** 코드포인트 기준 안전 절단(한글/이모지 깨짐 방지) */
    private static String truncateByCodePoint(String src, int maxCodePoints) {
        if (src == null) return "";
        int len = src.codePointCount(0, src.length());
        if (len <= maxCodePoints) return src;
        int endIdx = src.offsetByCodePoints(0, maxCodePoints);
        return src.substring(0, endIdx);
    }
}
