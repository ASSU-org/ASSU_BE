package com.assu.server.domain.auth.service;


import com.assu.server.domain.auth.dto.ssu.USaintAuthRequest;
import com.assu.server.domain.auth.dto.ssu.USaintAuthResponse;

public interface SSUAuthService {
    USaintAuthResponse uSaintAuth(USaintAuthRequest uSaintAuthRequest);
}
