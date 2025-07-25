package com.assu.server.domain.chat.entity;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChattingRoom extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Enumerated(EnumType.STRING)
	private ActivationStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Admin admin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partner_id")
	private Partner partner;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private List<Message> messages;
}