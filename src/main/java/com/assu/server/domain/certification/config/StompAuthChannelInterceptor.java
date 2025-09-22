package com.assu.server.domain.certification.config;

import com.assu.server.domain.auth.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j; // SLF4j 로그 추가

@Slf4j // SLF4j 어노테이션 추가
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

	private final JwtUtil jwtUtil;
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			String authHeader = accessor.getFirstNativeHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = jwtUtil.getTokenFromHeader(authHeader);
				Authentication authentication = jwtUtil.getAuthentication(token);

				// ⭐️ 이 부분을 수정
				accessor.setUser(authentication);

				// ⭐️ 추가: 메시지 헤더에도 Authentication 정보 저장
				accessor.setHeader(StompHeaderAccessor.USER_HEADER, authentication);

				log.info("Authentication set: {}", authentication);
			}
		}

		return message;
	}

	// @Override
	// public Message<?> preSend(Message<?> message, MessageChannel channel) {
	// 	StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
	// 	log.info("StompCommand: {}", accessor.getCommand()); // StompCommand 로그 추가
	//
	// 	if (StompCommand.CONNECT.equals(accessor.getCommand())) {
	// 		log.info("CONNECT command received.");
	// 		// 프론트에서 connect 시 Authorization 헤더 넣어야 함
	// 		String authHeader = accessor.getFirstNativeHeader("Authorization");
	// 		log.info("Authorization Header: {}", authHeader); // Authorization 헤더 로그 추가
	//
	// 		if (authHeader != null && authHeader.startsWith("Bearer ")) {
	// 			String token = jwtUtil.getTokenFromHeader(authHeader);
	// 			log.info("Extracted Token: {}", token); // 추출된 토큰 로그 추가
	//
	// 			// JwtUtil 이용해서 Authentication 복원
	// 			Authentication authentication = jwtUtil.getAuthentication(token);
	// 			log.info("Authentication restored: {}", authentication); // 복원된 인증 정보 로그 추가
	//
	// 			// WebSocket 세션에 Authentication(UserPrincipal) 저장
	// 			accessor.setUser(authentication);
	// 			log.info("User principal set on accessor.");
	// 		} else {
	// 			log.warn("Authorization header is missing or not in Bearer format.");
	// 		}
	// 	} else if (StompCommand.SEND.equals(accessor.getCommand())) {
	// 		// SEND 명령어에 대한 로그 추가 (메시지 전송 시)
	// 		Object payload = message.getPayload();
	// 		log.info("SEND command received. Destination: {}, Payload: {}", accessor.getDestination(), payload);
	// 	}
	//
	// 	return message;
	// }
}