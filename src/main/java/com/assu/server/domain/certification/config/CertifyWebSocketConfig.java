package com.assu.server.domain.certification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
public class CertifyWebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/certification/progress"); // 인증현황을 받아보기 위한 구독 주소
		config.setApplicationDestinationPrefixes("/certification"); // 클라이언트가 인증 요청을 보내는 주소
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")           // 클라이언트 WebSocket 연결 주소
			.setAllowedOriginPatterns("*").withSockJS(); // CORS 허용
	}
}
