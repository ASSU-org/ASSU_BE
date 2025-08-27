package com.assu.server.domain.partnership.repository;

import com.assu.server.domain.partnership.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
}
