package com.assu.server.domain.certification.component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class CertificationSessionManager {
	private final Map<Long, Set<Long>> sessionUserMap = new ConcurrentHashMap<>();

	public void openSession(Long sessionId) {
		sessionUserMap.put(sessionId, ConcurrentHashMap.newKeySet());
	}

	public void addUserToSession(Long sessionId, Long userId) {
		sessionUserMap.getOrDefault(sessionId, ConcurrentHashMap.newKeySet()).add(userId);
	}

	public int getCurrentUserCount(Long sessionId) {
		return sessionUserMap.getOrDefault(sessionId, Set.of()).size();
	}

	public boolean hasUser(Long sessionId, Long userId) {
		return sessionUserMap.getOrDefault(sessionId, Set.of()).contains(userId);
	}
	public List<Long> snapshotUserIds(Long sessionId) {
		return List.copyOf(sessionUserMap.getOrDefault(sessionId, Set.of()));
	}

	public void removeSession(Long sessionId) {
		sessionUserMap.remove(sessionId);
	}
}
