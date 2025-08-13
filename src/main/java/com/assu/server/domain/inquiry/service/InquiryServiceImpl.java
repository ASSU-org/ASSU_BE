package com.assu.server.domain.inquiry.service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.inquiry.converter.InquiryConverter;
import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import com.assu.server.domain.inquiry.entity.Inquiry;
import com.assu.server.domain.inquiry.entity.Inquiry.Status;
import com.assu.server.domain.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryConverter inquiryConverter;
    private final MemberRepository memberRepository;

    /** 문의 등록 */
    @Transactional
    @Override
    public Long create(InquiryCreateRequestDTO req, Long memberId) {
        Member member = memberRepository.getReferenceById(memberId);

        Inquiry inquiry = Inquiry.builder()
                .member(member)
                .title(req.getTitle())
                .content(req.getContent())
                .email(req.getEmail())
                .status(Status.WAITING)
                .build();

        inquiryRepository.save(inquiry);
        return inquiry.getId();
    }

    /** 문의 내역 조회 (status=all|waiting|answered) */
    @Transactional(readOnly = true)
    @Override
    public Page<InquiryResponseDTO> list(String status, Pageable pageable, Long memberId) {
        Page<Inquiry> page = switch (status.toLowerCase()) {
            case "waiting" -> inquiryRepository.findByMemberIdAndStatus(memberId, Status.WAITING, pageable);
            case "answered" -> inquiryRepository.findByMemberIdAndStatus(memberId, Status.ANSWERED, pageable);
            case "all"      -> inquiryRepository.findByMemberId(memberId, pageable);
            default         -> throw new IllegalArgumentException("status must be one of [all, waiting, answered]");
        };

        return page.map(inquiryConverter::toDto);
    }

    /** 단건 상세 조회 */
    @Transactional(readOnly = true)
    @Override
    public InquiryResponseDTO get(Long id, Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        if (!inquiry.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("not yours");
        }
        return inquiryConverter.toDto(inquiry);
    }

    /** 운영자가 답변 완료 처리 */
    @Transactional
    @Override
    public void markAnswered(Long id) {
        Inquiry i = inquiryRepository.findById(id).orElseThrow();
        i.markAnswered();
        // TODO: 필요 시 '답변 완료' 알림 발송
    }
}