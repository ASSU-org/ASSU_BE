// package com.assu.server.domain.chat.config;
//
// import org.springframework.context.annotation.Configuration;
// import org.springframework.messaging.simp.config.ChannelRegistration;
// import org.springframework.messaging.simp.config.MessageBrokerRegistry;
// import org.springframework.web.socket.config.annotation.*;
//
// @Configuration
// @EnableWebSocketMessageBroker
// public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//     @Override
//     public void registerStompEndpoints(StompEndpointRegistry registry) {
//         registry.addEndpoint("/ws")  // 클라이언트 WebSocket 연결 지점
//                 .setAllowedOriginPatterns(
//                         "*",
//                         "https://assu.shop",
//                         "http://localhost:63342",
//                         "http://localhost:5173",     // Vite 기본
//                         "http://localhost:3000",     // CRA/Next 기본
//                         "http://127.0.0.1:*",
//                         "http://192.168.*.*:*");       // 같은 LAN의 실제 기기 테스트용// fallback for old browsers
//         // 같은 LAN의 실제 기기 테스트용
//                       // fallback for old browsers
//
//         // ✅ 모바일/안드로이드용 (네이티브 WebSocket)
//         registry.addEndpoint("/ws")
//                 .setAllowedOriginPatterns("*"); // wss 사용 시 TLS 세팅
//     }
//
//     @Override
//     public void configureMessageBroker(MessageBrokerRegistry registry) {
//         registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 보내는 prefix
//         registry.enableSimpleBroker("/sub"); // 서버가 보내는 prefix
//     }
// }
package com.assu.server.domain.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.assu.server.domain.certification.config.StompAuthChannelInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")  // 클라이언트 WebSocket 연결 지점
                .setAllowedOriginPatterns(
                        "*",
                        "https://assu.shop",
                        "http://localhost:63342",
                        "http://localhost:5173",     // Vite 기본
                        "http://localhost:3000",     // CRA/Next 기본
                        "http://127.0.0.1:*",
                        "http://192.168.*.*:*");
        // 채팅용 엔드포인트

        // 인증용 엔드포인트
        registry.addEndpoint("/ws-certify")
            .setAllowedOriginPatterns("*");

        registry.addEndpoint("/ws-certify/sock")
            .setAllowedOriginPatterns("*")
            .withSockJS(); // ⬅️ SockJS를 반드시 포함해야 합니다.

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 채팅용
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");

        // 인증용 추가
        registry.setApplicationDestinationPrefixes("/pub", "/app"); // 둘 다 추가
        registry.enableSimpleBroker("/sub", "/certification"); // 둘 다 추가
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }
}