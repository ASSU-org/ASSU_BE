package com.assu.server.domain.notification.entity;
import com.assu.server.domain.common.entity.BaseEntity;

import com.assu.server.domain.common.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false)
	private Member receiver;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private NotificationType type;

	// 원천 도메인의 식별자(폴리모픽 FK를 애플리케이션 레벨에서 관리)
	@Column(nullable = false)
	private Long refId;

	// 목록용 스냅샷(조인 없이 렌더)
	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String messagePreview;

	private String deeplink; // ex) /chat/rooms/123

	@Column(nullable = false)
	private boolean isRead = false;

	@Column(nullable = true)
	private LocalDateTime readAt;

	public void markRead() {
		if (!this.isRead) {
			this.isRead = true;
			this.readAt = LocalDateTime.now();
		}
	}
}