package com.trademaster.ims.mobile.auth.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;

public final class CustomerAuthDtos {
 private CustomerAuthDtos(){}
 public static final String PASSWORD_MESSAGE="Password must be 8-72 characters and include uppercase, lowercase, number, and special character.";
 public record RegisterRequest(@NotBlank @Size(min=2,max=120) String fullName,@NotBlank @Email @Size(max=254) String email,@NotBlank @Size(max=30) String phone,@NotBlank @Size(min=8,max=72) String password,@NotBlank String confirmPassword,@NotNull @AssertTrue(message="Terms and Privacy acknowledgement is required") Boolean termsAccepted){}
 public record EmailOtpRequest(@NotBlank @Email @Size(max=254) String email,@NotBlank @Pattern(regexp="\\d{6}",message="A six-digit verification code is required") String otp){}
 public record EmailRequest(@NotBlank @Email @Size(max=254) String email){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password,String deviceDescription){}
 public record RefreshRequest(@NotBlank String refreshToken,String deviceDescription){}
 public record LogoutRequest(String refreshToken){}
 public record ResetPasswordRequest(@NotBlank String resetToken,@NotBlank @Size(min=8,max=72) String newPassword,@NotBlank String confirmPassword){}
 public record ProfileUpdateRequest(@NotBlank @Size(min=2,max=120) String fullName,@NotBlank @Size(max=30) String phone,@Size(max=1000) String address,@Size(max=100) String city,@Size(max=100) String state,@Size(max=20) String postalCode,@Size(max=100) String country){}
 public record ChangePasswordRequest(@NotBlank String currentPassword,@NotBlank @Size(min=8,max=72) String newPassword,@NotBlank String confirmPassword){}
 public record PendingVerificationResponse(String maskedEmail,long otpExpiresInSeconds,long resendAvailableInSeconds){}
 public record CustomerProfileResponse(Long customerId,String customerCode,String fullName,String email,String phone,String customerType,String address,String city,String state,String postalCode,String country,String photoUrl,boolean emailVerified){}
 public record AuthResponse(String accessToken,String refreshToken,Instant accessTokenExpiresAt,Instant refreshTokenExpiresAt,CustomerProfileResponse profile){@Override public String toString(){return "AuthResponse{profile="+profile+", tokens=[REDACTED]}";}}
 public record TokenResponse(String accessToken,String refreshToken,Instant accessTokenExpiresAt,Instant refreshTokenExpiresAt){@Override public String toString(){return "TokenResponse{tokens=[REDACTED]}";}}
 public record ResetAuthorizationResponse(String resetToken,Instant expiresAt){@Override public String toString(){return "ResetAuthorizationResponse{token=[REDACTED], expiresAt="+expiresAt+"}";}}
 public record MessageResponse(String message){}
}
