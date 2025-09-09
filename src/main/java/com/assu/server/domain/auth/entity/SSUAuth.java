package com.assu.server.domain.auth.entity;

import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ssu_auth",
        indexes = {
                @Index(name = "ux_ssu_auth_student_id", columnList = "student_number", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SSUAuth extends BaseEntity {

    @Id
    @Column(name = "member_id")
    private Long id;

    @OneToOne @MapsId
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Column(name = "student_number", length = 20, nullable = false)
    private String studentNumber;

    @Column(name = "is_authenticated", nullable = false)
    private Boolean isAuthenticated = Boolean.FALSE;

    @Column(name = "authenticated_at")
    private LocalDateTime authenticatedAt;
}
