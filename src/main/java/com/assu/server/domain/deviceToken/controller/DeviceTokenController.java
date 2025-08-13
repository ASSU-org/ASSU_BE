package com.assu.server.domain.deviceToken.controller;

import com.assu.server.domain.common.entity.Member;
import com.assu.server.domain.deviceToken.service.DeviceTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("deviceTokens")
@RequiredArgsConstructor
public class DeviceTokenController {
    private final DeviceTokenService service;

    public record RegisterTokenReq(String token) {}

    @PostMapping("/register")
    public void register(@RequestBody RegisterTokenReq req,
                         @RequestParam Long MemberId)
    {
        service.register(req.token(), MemberId);
    }

    @DeleteMapping("/unregister/{token_id}")
    public void unregister(@PathVariable String token_id){
        service.unregister(token_id);
    }
}
