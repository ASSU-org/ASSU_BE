package com.assu.server.domain.partnership.entity;
import com.assu.server.domain.common.entity.BaseEntity;
import com.assu.server.domain.partnership.entity.enums.CriterionType;
import com.assu.server.domain.partnership.entity.enums.OptionType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


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
	private CriterionType criterionType;

	@Enumerated(EnumType.STRING)
	private OptionType optionType;

	private Integer people;

	private Long cost;

	private String category;

	private Long discount;

	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Goods> goods = new ArrayList<>();

}
