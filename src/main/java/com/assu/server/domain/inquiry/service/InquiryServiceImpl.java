package com.assu.server.domain.inquiry.service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.common.repository.MemberRepository;
import com.assu.server.domain.inquiry.converter.InquiryConverter;
import com.assu.server.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.assu.server.domain.inquiry.dto.InquiryResponseDTO;
import com.assu.server.domain.inquiry.entity.Inquiry;
import com.assu.server.domain.inquiry.entity.Inquiry.Status;
import com.assu.server.domain.inquiry.repository.InquiryRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    /** 문의 등록 */
    @Transactional
    @Override
    public Long create(InquiryCreateRequestDTO req, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));

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
    public Map<String, Object> getInquiries(String status, int page, int size, Long memberId) {
        if (page < 1) throw new DatabaseException(ErrorStatus.PAGE_UNDER_ONE);
        if (size < 1 || size > 200) throw new DatabaseException(ErrorStatus.PAGE_SIZE_INVALID);

        String s = status.toLowerCase();
        if (!s.equals("all") && !s.equals("waiting") && !s.equals("answered")) {
            throw new DatabaseException(ErrorStatus.INVALID_INQUIRY_STATUS_FILTER);
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<InquiryResponseDTO> p = list(s, pageable, memberId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("items", p.getContent());
        body.put("page", p.getNumber() + 1);
        body.put("size", p.getSize());
        body.put("totalPages", p.getTotalPages());
        body.put("totalElements", p.getTotalElements());
        return body;
    }

    @Override
    public Page<InquiryResponseDTO> list(String status, Pageable pageable, Long memberId) {
        Page<Inquiry> page = switch (status.toLowerCase()) {
            case "waiting"  -> inquiryRepository.findByMemberIdAndStatus(memberId, Status.WAITING, pageable);
            case "answered" -> inquiryRepository.findByMemberIdAndStatus(memberId, Status.ANSWERED, pageable);
            case "all"      -> inquiryRepository.findByMemberId(memberId, pageable);
            default         -> throw new DatabaseException(ErrorStatus.INVALID_INQUIRY_STATUS_FILTER);
        };
        return page.map(InquiryConverter::toDto);
    }

    /** 단건 상세 조회 */
    @Transactional(readOnly = true)
    @Override
    public InquiryResponseDTO get(Long id, Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_INQUIRY));

        if (!inquiry.getMember().getId().equals(memberId)) {
            throw new DatabaseException(ErrorStatus.FORBIDDEN_INQUIRY);
        }
        return InquiryConverter.toDto(inquiry);
    }

    /** 답변 저장(상태 ANSWERED 전환) */
    @Transactional
    @Override
    public void answer(Long inquiryId, String answerText) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_INQUIRY));

        if (inquiry.getStatus() == Inquiry.Status.ANSWERED) {
            throw new DatabaseException(ErrorStatus.ALREADY_ANSWERED);
        }

        inquiry.answer(answerText);
    }
}