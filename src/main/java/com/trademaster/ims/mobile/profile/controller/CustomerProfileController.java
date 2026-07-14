package com.trademaster.ims.mobile.profile.controller;

import com.trademaster.ims.mobile.auth.dto.CustomerAuthDtos.CustomerProfileResponse;
import com.trademaster.ims.mobile.auth.security.CustomerAuthentication;
import com.trademaster.ims.mobile.auth.service.CustomerAuthException;
import com.trademaster.ims.mobile.common.response.MobileApiResponse;
import com.trademaster.ims.mobile.profile.service.CustomerProfilePhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mobile/v1/profile")
public class CustomerProfileController {
    private final CustomerProfilePhotoService photos;

    public CustomerProfileController(CustomerProfilePhotoService photos) {
        this.photos = photos;
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MobileApiResponse<CustomerProfileResponse> uploadPhoto(
            Authentication authentication,
            @RequestPart("photo") MultipartFile photo) {
        return MobileApiResponse.success(photos.upload(accountId(authentication), photo));
    }

    private Long accountId(Authentication authentication) {
        if (authentication instanceof CustomerAuthentication customer) {
            return customer.accountId();
        }
        throw new CustomerAuthException(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_REQUIRED",
                "Authentication is required.");
    }
}