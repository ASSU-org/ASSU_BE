package com.assu.server.domain.auth.dto.ssu;

import com.assu.server.domain.user.entity.enums.Major;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class USaintAuthResponse {
    private String studentNumber;
    private String name;
    private String enrollmentStatus;
    private String yearSemester;
    private Major major;
}
