package com.assu.server.domain.report.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    // 사용자 신고용
    STUDENT_USER_SPAM("스팸/홍보"),
    STUDENT_USER_INAPPROPRIATE_CONTENT("부적절한 내용"),
    STUDENT_USER_HARASSMENT("괴롭힘/욕설"),
    STUDENT_USER_FRAUD("사기/부정행위"),
    STUDENT_USER_PRIVACY_VIOLATION("개인정보 침해"),
    STUDENT_USER_OTHER("기타"),

    // 리뷰 신고용
    REVIEW_INAPPROPRIATE_CONTENT("부적절한 내용 및 욕설이 포함된 리뷰에요"),
    REVIEW_FALSE_INFORMATION("허위사실 / 거짓이 포함된 리뷰에요"),
    REVIEW_SPAM("홍보 / 광고를 위한 리뷰에요"),

    // 건의글 신고용
    SUGGESTION_INAPPROPRIATE_CONTENT("부적절한 내용 및 욕설이 포함된 건의글이에요"),
    SUGGESTION_FALSE_INFORMATION("허위사실 / 거짓이 포함된 건의글에요"),
    SUGGESTION_SPAM("홍보/광고를 위한 건의글이에요 ");

    private final String description;
}
