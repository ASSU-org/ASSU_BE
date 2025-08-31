package com.assu.server.domain.user.converter;

import com.assu.server.domain.user.dto.StudentResponseDTO;
import com.assu.server.domain.user.entity.Student;

public class StudentConverter {
        public static StudentResponseDTO.CheckStampResponseDTO checkStampResponseDTO(Student student, String message) {
            return StudentResponseDTO.CheckStampResponseDTO.builder()
                    .userId(student.getId())
                    .stamp(student.getStamp())
                    .message(message)
                    .build();
        }
}
