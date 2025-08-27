package com.assu.server.domain.user.service;

import com.assu.server.domain.user.dto.StudentResponseDTO;

public interface StudentService {
    StudentResponseDTO.CheckStampResponseDTO getStamp(Long memberId);//조회
}
