package com.assu.server.domain.notification.service;

import com.assu.server.domain.notification.repository.NotificationOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxStatusService {
    private final NotificationOutboxRepository repo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markDispatched(Long id) {
        int updated = repo.markDispatchedById(id);
        log.info("[OutboxStatus] DISPATCHED updated={} outboxId={}", updated, id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSent(Long id) {
        int updated = repo.markSentById(id);
        log.info("[OutboxStatus] SENT updated={} outboxId={}", updated, id);
    }
}
