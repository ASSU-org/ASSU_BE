package com.assu.server.domain.admin.service;

import com.assu.server.domain.admin.dto.AdminResponseDTO;

public interface AdminService {

    AdminResponseDTO.RandomPartnerResponseDTO suggestRandomPartner();
}
