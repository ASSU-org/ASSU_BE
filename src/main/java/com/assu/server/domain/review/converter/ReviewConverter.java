package com.assu.server.domain.review.converter;

import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.review.dto.ReviewRequestDTO;
import com.assu.server.domain.review.dto.ReviewResponseDTO;
import com.assu.server.domain.review.entity.Review;
import com.assu.server.domain.review.entity.ReviewPhoto;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.user.entity.Student;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

public class ReviewConverter {
    public static ReviewResponseDTO.WriteReviewResponseDTO writeReviewResultDTO(Review review){
        //enti -> dto
        return ReviewResponseDTO.WriteReviewResponseDTO.builder()
                .reviewId(review.getId())// 리스폰스 dto로 아이디를 바꿔줄거다.
                .rate(review.getRate())
                .content(review.getContent())
//                .memberId(review.getStudent().getId())
                .createdAt(review.getCreatedAt())
                .reviewImageUrls(review.getImageList().stream()
                        .map(ReviewPhoto::getPhotoUrl)
                        .collect(Collectors.toList()))
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
    public static ReviewResponseDTO.CheckStudentReviewResponseDTO checkStudentReviewResultDTO(Review review){
        return ReviewResponseDTO.CheckStudentReviewResponseDTO.builder()
                .reviewId(review.getId())
                .rate(review.getRate())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .storeId(review.getStore().getId())
                .reviewImageUrls(review.getImageList().stream()
                        .map(ReviewPhoto::getPhotoUrl)
                        .collect(Collectors.toList()))
                .build();
    }
    // public static List<ReviewResponseDTO.CheckStudentReviewResponseDTO> checkStudentReviewResultDTO(List<Review> reviews){
    //     return reviews.stream()
    //             .map(ReviewConverter::checkStudentReviewResultDTO)
    //             .collect(Collectors.toList());
    // }

    public static Page<ReviewResponseDTO.CheckStudentReviewResponseDTO> checkStudentReviewResultDTO(Page<Review> reviews){
        return reviews.map(ReviewConverter::checkStudentReviewResultDTO);
    }

    public static ReviewResponseDTO.CheckPartnerReviewResponseDTO checkPartnerReviewResultDTO(Review review){
        return ReviewResponseDTO.CheckPartnerReviewResponseDTO.builder()
                .reviewId(review.getId())
                .storeId(review.getStore().getId())
                .reviewerId(review.getStudent().getId())
                .content(review.getContent())
                .rate(review.getRate())
                .createdAt(review.getCreatedAt())
                .reviewImageUrls(review.getImageList().stream()
                        .map(ReviewPhoto::getPhotoUrl)
                        .collect(Collectors.toList()))
                .build();

    }
    // public static List<ReviewResponseDTO.CheckPartnerReviewResponseDTO> checkPartnerReviewResultDTO(List<Review> reviews){
    //     return reviews.stream()
    //             .map(ReviewConverter::checkPartnerReviewResultDTO)
    //             .collect(Collectors.toList());
    // }

    public static Page<ReviewResponseDTO.CheckPartnerReviewResponseDTO> checkPartnerReviewResultDTO(Page<Review> reviews){
        return reviews.map(ReviewConverter::checkPartnerReviewResultDTO);
    }
    public static ReviewResponseDTO.DeleteReviewResponseDTO deleteReviewResultDTO(Long reviewId){
        return ReviewResponseDTO.DeleteReviewResponseDTO.builder()
                .reviewId(reviewId)
                .build();
    }
}