package com.trademaster.ims.mobile.auth.controller;

import com.trademaster.ims.mobile.auth.dto.CustomerAuthDtos.*;
import com.trademaster.ims.mobile.auth.security.CustomerAuthentication;
import com.trademaster.ims.mobile.auth.service.CustomerAuthService;
import com.trademaster.ims.mobile.common.response.MobileApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile/v1/auth")
public class CustomerAuthController {
 private final CustomerAuthService service;
 public CustomerAuthController(CustomerAuthService service){this.service=service;}
 @PostMapping("/register") public ResponseEntity<MobileApiResponse<PendingVerificationResponse>> register(@Valid @RequestBody RegisterRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(MobileApiResponse.success(service.register(r)));}
 @PostMapping("/verify-registration") public MobileApiResponse<AuthResponse> verify(@Valid @RequestBody EmailOtpRequest r){return MobileApiResponse.success(service.verifyRegistration(r));}
 @PostMapping("/resend-registration-otp") public MobileApiResponse<PendingVerificationResponse> resend(@Valid @RequestBody EmailRequest r){return MobileApiResponse.success(service.resendRegistration(r));}
 @PostMapping("/login") public MobileApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest r){return MobileApiResponse.success(service.login(r));}
 @PostMapping("/refresh") public MobileApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest r){return MobileApiResponse.success(service.refresh(r));}
 @PostMapping("/logout") public MobileApiResponse<MessageResponse> logout(Authentication a,@RequestBody LogoutRequest r){return MobileApiResponse.success(service.logout(id(a),r));}
 @PostMapping("/forgot-password") public MobileApiResponse<MessageResponse> forgot(@Valid @RequestBody EmailRequest r){return MobileApiResponse.success(service.forgotPassword(r));}
 @PostMapping("/verify-password-reset-otp") public MobileApiResponse<ResetAuthorizationResponse> verifyReset(@Valid @RequestBody EmailOtpRequest r){return MobileApiResponse.success(service.verifyResetOtp(r));}
 @PostMapping("/reset-password") public MobileApiResponse<MessageResponse> reset(@Valid @RequestBody ResetPasswordRequest r){return MobileApiResponse.success(service.resetPassword(r));}
 @GetMapping("/me") public MobileApiResponse<CustomerProfileResponse> me(Authentication a){return MobileApiResponse.success(service.profile(id(a)));}
 @PutMapping("/me") public MobileApiResponse<CustomerProfileResponse> update(Authentication a,@Valid @RequestBody ProfileUpdateRequest r){return MobileApiResponse.success(service.updateProfile(id(a),r));}
 @PostMapping("/change-password") public MobileApiResponse<MessageResponse> change(Authentication a,@Valid @RequestBody ChangePasswordRequest r){return MobileApiResponse.success(service.changePassword(id(a),r));}
 private Long id(Authentication a){if(!(a instanceof CustomerAuthentication c))throw new com.trademaster.ims.mobile.auth.service.CustomerAuthException(HttpStatus.UNAUTHORIZED,"AUTHENTICATION_REQUIRED","Authentication is required.");return c.accountId();}
}
