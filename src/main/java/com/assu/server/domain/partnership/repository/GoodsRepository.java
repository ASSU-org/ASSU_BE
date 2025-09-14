package com.assu.server.domain.partnership.repository;

import java.util.List;
import java.util.Optional;

import com.assu.server.domain.partnership.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

	List<Goods> findByContentId(Long contentId);

	List<Goods> findByContentIdIn(List<Long> contentIds);
}
