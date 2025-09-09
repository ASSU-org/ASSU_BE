package com.assu.server.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name="notification_id", nullable=false, unique=true)
    private Notification notification;

    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private Status status; // PENDING, SENT, FAILED

    @Column(nullable=false) private int retryCount;

    public enum Status { PENDING, SENDING, DISPATCHED, SENT, FAILED }

    public void markSending()    { this.status = Status.SENDING; }
    public void markDispatched() { this.status = Status.DISPATCHED; }
    public void markSent()       { this.status = Status.SENT; }
    public void markFailed()     { this.status = Status.FAILED; }
    public void incRetry()       { this.retryCount++; }
}
