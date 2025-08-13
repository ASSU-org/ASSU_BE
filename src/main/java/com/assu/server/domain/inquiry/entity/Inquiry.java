package com.assu.server.domain.inquiry.entity;

import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.common.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Status status;

    private LocalDateTime answeredAt;

    public enum Status { WAITING, ANSWERED }

    // 상태 전환
    public void markAnswered() {
        this.status = Status.ANSWERED;
        this.answeredAt = LocalDateTime.now();
    }
}
