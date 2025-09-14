// package com.assu.server.domain.certification.handler;
//
// import org.springframework.stereotype.Component;
// import org.springframework.web.socket.CloseStatus;
// import org.springframework.web.socket.TextMessage;
// import org.springframework.web.socket.WebSocketSession;
// import org.springframework.web.socket.handler.TextWebSocketHandler;
//
// @Component
// public class CertificationWebsocketHandler extends TextWebSocketHandler {
//
// 	@Override
// 	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
// 		// 클라이언트 연결이 성공적으로 수립되었을 때 호출됩니다.
// 		System.out.println("Client connected: " + session.getId());
// 	}
//
// 	@Override
// 	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
// 		// 클라이언트로부터 텍스트 메시지를 받았을 때 호출됩니다.
// 		String payload = message.getPayload();
// 		System.out.println("Message received from " + session.getId() + ": " + payload);
//
// 		// 받은 메시지를 다시 클라이언트에게 보내거나 다른 로직을 처리합니다.
// 		session.sendMessage(new TextMessage("Echo: " + payload));
// 	}
//
// 	@Override
// 	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
// 		// 클라이언트 연결이 종료되었을 때 호출됩니다.
// 		System.out.println("Client disconnected: " + session.getId() + " with status " + status.getCode());
// 	}
//
// 	@Override
// 	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
// 		// 전송 오류가 발생했을 때 호출됩니다.
// 		System.err.println("Transport error for session " + session.getId() + ": " + exception.getMessage());
// 		// 필요한 경우 연결을 종료하거나 오류를 처리합니다.
// 		session.close(CloseStatus.SERVER_ERROR);
// 	}
// }