package com.trademaster.ims.mobile.catalog.controller;

import com.trademaster.ims.mobile.catalog.dto.MobileHealthResponse;
import com.trademaster.ims.mobile.common.response.MobileApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mobile/v1")
public class MobileHealthController {
    @GetMapping("/health")
    public MobileApiResponse<MobileHealthResponse> health() {
        return MobileApiResponse.success(new MobileHealthResponse("TechSmart Shop Mobile API", "UP", "v1"));
    }
}
