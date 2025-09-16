//package com.assu.server.domain.chat.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.*;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")  // 클라이언트 WebSocket 연결 지점
//                .setAllowedOriginPatterns(
//                        "*",
//                        "https://assu.shop",
//                        "http://localhost:63342",
//                        "http://localhost:5173",     // Vite 기본
//                        "http://localhost:3000",     // CRA/Next 기본
//                        "http://127.0.0.1:*",
//                        "http://192.168.*.*:*");       // 같은 LAN의 실제 기기 테스트용
//                      // fallback for old browsers
//
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 보내는 prefix
//        registry.enableSimpleBroker("/sub"); // 서버가 보내는 prefix
//        registry.enableSimpleBroker("/certification"); // 인증현황을 받아보기 위한 구독 주소
//        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 인증 요청을 보내는 주소
//    }
//}
