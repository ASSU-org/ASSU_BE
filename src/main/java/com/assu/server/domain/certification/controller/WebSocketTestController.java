package com.assu.server.domain.certification.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebSocketTestController {

	@MessageMapping("/test")
	public void test(@Payload String payload) {
		log.info("### 테스트용 메시지 수신 성공! 페이로드: {}", payload);
	}
}
