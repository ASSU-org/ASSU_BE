package com.assu.server.domain.notification.repository;

import com.assu.server.domain.notification.entity.NotificationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
          update NotificationOutbox o
             set o.status = com.assu.server.domain.notification.entity.NotificationOutbox.Status.DISPATCHED
           where o.id = :id
             and o.status = com.assu.server.domain.notification.entity.NotificationOutbox.Status.PENDING
        """)
    int markDispatchedById(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
 update NotificationOutbox o
    set o.status = com.assu.server.domain.notification.entity.NotificationOutbox.Status.SENT
  where o.id = :id
    and o.status <> com.assu.server.domain.notification.entity.NotificationOutbox.Status.SENT
""")
    int markSentById(@Param("id") Long id);

    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query("""
        update NotificationOutbox o
           set o.status = com.assu.server.domain.notification.entity.NotificationOutbox.Status.FAILED
         where o.id = :id
           and o.status <> com.assu.server.domain.notification.entity.NotificationOutbox.Status.FAILED
        """)
    int markFailedById(@org.springframework.data.repository.query.Param("id") Long id);

    boolean existsByIdAndStatus(Long id, NotificationOutbox.Status status);

    List<NotificationOutbox> findTop50ByStatusOrderByIdAsc(NotificationOutbox.Status status);
}
