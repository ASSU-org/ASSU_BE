package com.assu.server.domain.notification.service;

import com.assu.server.domain.notification.entity.NotificationOutbox;
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

    @Transactional(readOnly = true)
    public boolean isAlreadySent(Long id) {
        return repo.existsByIdAndStatus(id, NotificationOutbox.Status.SENT);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long id) {
        int updated = repo.markFailedById(id);
        log.info("[OutboxStatus] FAILED updated={} outboxId={}", updated, id);
    }

}
