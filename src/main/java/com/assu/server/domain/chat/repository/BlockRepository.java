package com.assu.server.domain.chat.repository;


import com.assu.server.domain.chat.entity.Block;
import com.assu.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

    void deleteByBlockerAndBlocked(Member blocker, Member blocked);

    List<Block> findByBlocker(Member blocker);
}
