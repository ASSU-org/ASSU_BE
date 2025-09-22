package com.assu.server.domain.certification.component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//
// @Slf4j // ⭐️ SLF4j 로그 사용을 위해 추가
// @Component
// public class CertificationSessionManager {
// 	private final Map<Long, Set<Long>> sessionUserMap = new ConcurrentHashMap<>();
//
// 	public void openSession(Long sessionId) {
// 		sessionUserMap.put(sessionId, ConcurrentHashMap.newKeySet());
// 		// ⭐️ 로그 추가
// 		log.info("✅ New certification session opened. SessionID: {}", sessionId);
// 	}
//
// 	public void addUserToSession(Long sessionId, Long userId) {
// 		Set<Long> users = sessionUserMap.computeIfAbsent(sessionId, k -> {
// 			log.warn("Attempted to add user to a non-existent session. Creating new set for SessionID: {}", k);
// 			return ConcurrentHashMap.newKeySet();
// 		});
//
// 		boolean isAdded = users.add(userId);
//
// 		// ⭐️ 요청하신 멤버 추가 확인 로그
// 		if (isAdded) {
// 			log.info("👤 User added to session. SessionID: {}, UserID: {}. Current participants: {}",
// 				sessionId, userId, users.size());
// 		} else {
// 			log.info("👤 User already in session. SessionID: {}, UserID: {}. Current participants: {}",
// 				sessionId, userId, users.size());
// 		}
// 	}
//
// 	public int getCurrentUserCount(Long sessionId) {
// 		return sessionUserMap.getOrDefault(sessionId, Set.of()).size();
// 	}
//
// 	public boolean hasUser(Long sessionId, Long userId) {
// 		return sessionUserMap.getOrDefault(sessionId, Set.of()).contains(userId);
// 	}
//
// 	public List<Long> snapshotUserIds(Long sessionId) {
// 		return List.copyOf(sessionUserMap.getOrDefault(sessionId, Set.of()));
// 	}
//
//
//
// 	public void removeSession(Long sessionId) {
// 		sessionUserMap.remove(sessionId);
// 		// ⭐️ 로그 추가
// 		log.info("❌ Certification session removed. SessionID: {}", sessionId);
// 	}
// }
@Component
@RequiredArgsConstructor
public class CertificationSessionManager {

	// RedisTemplate을 주입받습니다.
	private final StringRedisTemplate redisTemplate;

	// 세션 ID를 위한 KEY를 만드는 헬퍼 메서드
	private String getKey(Long sessionId) {
		return "certification:session:" + sessionId;
	}

	public void openSession(Long sessionId) {
		String key = getKey(sessionId);
		// 세션을 연다는 것은 키를 만드는 것과 같습니다.
		// addUserToSession에서 자동으로 키가 생성되므로 이 메서드는 비워두거나,
		// 만료 시간 설정 등 초기화 로직을 넣을 수 있습니다.
		// 예: 10분 후 만료
		redisTemplate.expire(key, 10, TimeUnit.MINUTES);
	}

	public void addUserToSession(Long sessionId, Long userId) {
		String key = getKey(sessionId);
		// Redis의 Set 자료구조에 userId를 추가합니다.
		redisTemplate.opsForSet().add(key, String.valueOf(userId));
	}

	public int getCurrentUserCount(Long sessionId) {
		String key = getKey(sessionId);
		// Redis Set의 크기를 반환합니다.
		Long size = redisTemplate.opsForSet().size(key);
		return size != null ? size.intValue() : 0;
	}

	public boolean hasUser(Long sessionId, Long userId) {
		String key = getKey(sessionId);
		// Redis Set에 해당 멤버가 있는지 확인합니다.
		return redisTemplate.opsForSet().isMember(key, String.valueOf(userId));
	}

	public List<Long> snapshotUserIds(Long sessionId) {
		String key = getKey(sessionId);
		// Redis Set의 모든 멤버를 가져옵니다.
		Set<String> members = redisTemplate.opsForSet().members(key);
		if (members == null) {
			return List.of();
		}
		return members.stream()
			.map(Long::valueOf)
			.collect(Collectors.toList());
	}

	public void removeSession(Long sessionId) {
		String key = getKey(sessionId);
		// 세션 키 자체를 삭제합니다.
		redisTemplate.delete(key);
	}
}