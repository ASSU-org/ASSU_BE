package com.assu.server.domain.review.service;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partner.repository.PartnerRepository;
import com.assu.server.domain.review.converter.ReviewConverter;
import com.assu.server.domain.review.dto.ReviewRequestDTO;
import com.assu.server.domain.review.dto.ReviewResponseDTO;
import com.assu.server.domain.review.entity.Review;
import com.assu.server.domain.review.repository.ReviewRepository;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.user.entity.Student;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.domain.user.repository.StudentRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.exception.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final PartnerRepository partnerRepository;
    private final StudentRepository studentRepository;



    @Override
    public ReviewResponseDTO.WriteReviewResponseDTO writeReview(ReviewRequestDTO.WriteReviewRequestDTO request) {
        //Long memberId = SecurityUtil.getCurrentUserId;
        Long memberId = 1L;
        Long storeId = request.getStoreId(); //변수 선언
        //존재여부 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(()-> new DatabaseException(ErrorStatus.NO_SUCH_STORE)); //없을 경우!!
        Partner partner = partnerRepository.findById(request.getPartnerId()) //파라미터 변수 선언 없이 바로 받기
                .orElseThrow(()-> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));
        Student student = studentRepository.findById(Math.toIntExact(memberId))
                .orElseThrow(()-> new DatabaseException(ErrorStatus.NO_SUCH_STUDENT));

        Review review = ReviewConverter.toReviewEntity(request, store, partner, student);


        reviewRepository.save(review);//rep에서 데이터 상하차 저장
        //잘 저장 됏어요!!
        return ReviewConverter.writeReviewResultDTO(review);//객체를 dto로 바꿔서 사용자에게 보여줌 -> controller
    }

    @Override
    public List<ReviewResponseDTO.CheckStudentReviewResponseDTO> checkStudentReview() {
        //Long memberId = SecurityUtil.getCurrentUserId;
        Long memberId = 1L;
        List<Review> reviews = reviewRepository.findByMemberId(memberId);

        return ReviewConverter.checkStudentReviewResultDTO(reviews);
    }

    @Override
    @Transactional
    public List<ReviewResponseDTO.CheckPartnerReviewResponseDTO> checkPartnerReview() {
        //Long memberId = SecurityUtil.getCurrentUserId;
        Long partnerId = 2L; //ID 하드코딩 한 것

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));
        System.out.println("파트너 id는 "+partner.getId());
        Store store = storeRepository.findByPartner(partner)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STORE));

        //List<Review> reviews = reviewRepository.findByStoreId(store.getId());
        List<Review> reviews = reviewRepository.findByStoreIdOrderByCreatedAtDesc(store.getId()); //최신순 정렬
        return ReviewConverter.checkPartnerReviewResultDTO(reviews);
    }
    @Override
    @Transactional
    public ReviewResponseDTO.DeleteReviewResponseDTO deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
        return ReviewConverter.deleteReviewResultDTO(reviewId);
    }
}
