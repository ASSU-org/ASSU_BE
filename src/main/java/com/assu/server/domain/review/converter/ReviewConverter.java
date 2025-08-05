package com.assu.server.domain.review.converter;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.review.dto.ReviewRequestDTO;
import com.assu.server.domain.review.dto.ReviewResponseDTO;
import com.assu.server.domain.review.entity.Review;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.user.entity.Student;

public class ReviewConverter {
    public static ReviewResponseDTO.WriteReviewResponseDTO writeReviewResultDTO(Review review){
        //enti -> dto
        return ReviewResponseDTO.WriteReviewResponseDTO.builder()
                .reviewId(review.getId())// 리스폰스 dto로 아이디를 바꿔줄거다.
                .rate(review.getRate())
                .content(review.getContent())
//                .memberId(review.getStudent().getId())
                .createdAt(review.getCreatedAt())
                //한 리뷰 여러개 사진 but 하나로 묶임 추가 고려해보기 --추후에 !!
                .build(); //리스폰스 리턴
    }
    public static Review toReviewEntity(ReviewRequestDTO.WriteReviewRequestDTO  request, Store store, Partner partner, Student student){
        //request
        return Review.builder()
                .rate(request.getRate())
                .content(request.getContent())
                .store(store)
                .partner(partner)
                .student(student)
            //    .imageList(request.getReviewImage())
                .build();
    }
}
