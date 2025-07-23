package com.assu.server.domain.partnership.entity;
import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.partnership.entity.enums.PaperContentType;
import com.assu.server.domain.user.entity.enums.Major;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaperContent extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paper_id")
	private Paper paper;

	@Enumerated(EnumType.STRING)
	private PaperContentType type;

	private Integer people;

	private String belonging;

	private Long cost;

	private Long discount;

	private String goods;

	@Enumerated(EnumType.STRING)
	private Major major;

}
