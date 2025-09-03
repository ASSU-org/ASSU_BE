package com.assu.server.domain.certification.config;

import com.assu.server.domain.auth.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

	private final JwtUtil jwtUtil;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			// 프론트에서 connect 시 Authorization 헤더 넣어야 함
			String authHeader = accessor.getFirstNativeHeader("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = jwtUtil.getTokenFromHeader(authHeader);

				// JwtUtil 이용해서 Authentication 복원
				Authentication authentication = jwtUtil.getAuthentication(token);

				// WebSocket 세션에 Authentication(UserPrincipal) 저장
				accessor.setUser(authentication);
			}
		}

		return message;
	}
}