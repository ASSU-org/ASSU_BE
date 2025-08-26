package com.assu.server.domain.inquiry.repository;

import com.assu.server.domain.inquiry.entity.Inquiry;
import com.assu.server.domain.inquiry.entity.Inquiry.Status;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findByMemberId(Long memberId, Pageable pageable);
    Page<Inquiry> findByMemberIdAndStatus(Long memberId, Status status, Pageable pageable);
}
