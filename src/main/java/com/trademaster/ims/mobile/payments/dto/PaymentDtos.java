package com.trademaster.ims.mobile.payments.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class PaymentDtos {
 public record PaymentMethodResponse(String code,String displayName,String description,String type,boolean active,boolean eligible,String ineligibilityReason,BigDecimal minAmount,BigDecimal maxAmount,String supportedCurrency,boolean requiresReference,boolean requiresProof,String customerInstructions,boolean autoVerify,boolean reviewRequired){}
 public record PaymentInitiationRequest(@NotBlank @Size(max=50)String paymentMethodCode,@NotBlank @Size(max=100)String idempotencyKey,@Size(max=120)String returnTarget,@Size(max=250)String clientMetadata){}
 public record PaymentInitiationResult(String paymentNumber,String orderNumber,String paymentStatus,String attemptStatus,String methodCode,String methodType,BigDecimal amount,String currency,String provider,String gatewaySessionId,String redirectUrl,Instant expiresAt,String customerMessage){}
 public record ManualPaymentRequest(@NotBlank @Size(max=50)String paymentMethodCode,@NotBlank @Size(max=150)String transactionReference,@NotNull @DecimalMin("0.01")BigDecimal submittedAmount,@Size(max=120)String payerName,@Size(max=30)String payerPhone,@Size(max=500)String customerNote,@NotBlank @Size(max=100)String idempotencyKey){}
 public record ManualPaymentResult(String paymentNumber,String orderNumber,String paymentStatus,String reviewStatus,BigDecimal amount,String currency,String transactionReference,Instant submittedAt,String customerMessage){}
 public record CodSelectionRequest(@NotBlank @Size(max=100)String idempotencyKey){}
 public record CodSelectionResult(String paymentNumber,String orderNumber,String paymentStatus,String orderStatus,BigDecimal amount,String currency,String customerMessage){}
 public record PaymentStatusResult(String paymentNumber,String orderNumber,String paymentStatus,String accountingStatus,String methodCode,String methodType,BigDecimal amount,String currency,String customerMessage,boolean retryAllowed,boolean cancellable,List<PaymentAttemptSummary> attempts,List<PaymentTimelineEntry> timeline){}
 public record PaymentAttemptSummary(int attemptNumber,String status,String method,String provider,String gatewaySessionId,String externalReference,Instant expiresAt,Instant createdAt){}
 public record PaymentTimelineEntry(String status,String source,String note,Instant occurredAt,String actorType){}
 public record WebhookResult(String provider,String eventId,String processingStatus,String paymentNumber,String paymentStatus,String orderStatus,String message){}
 public record ReviewSummary(Long reviewId,String paymentNumber,String orderNumber,String status,String reasonCode,BigDecimal amount,String currency,Instant createdAt,String customerVisibleNote){}
 public record ReviewDecisionRequest(BigDecimal approvedAmount,Long accountId,@Size(max=500)String note){}
 public record ReconciliationRequest(@NotBlank String provider,@NotBlank String settlementReference,@NotNull BigDecimal settledAmount){}
 public record ReconciliationResult(String paymentNumber,String status,String message){}
 public record RefundRequest(@NotNull @DecimalMin("0.01")BigDecimal amount,@NotBlank @Size(max=100)String idempotencyKey,@NotBlank @Size(max=500)String reason){}
 public record RefundResult(String paymentNumber,String refundStatus,BigDecimal requestedAmount,String message){}
 public record MobileWalletProviderResponse(String code,String displayName,String shortDescription,boolean eligible,String ineligibilityReason,int displayOrder,String iconAssetKey,String visualThemeKey,String phoneLabel,String phoneHint,boolean requiresVerificationCode,boolean requiresPaymentPin,String instructions,BigDecimal minAmount,BigDecimal maxAmount,String supportedCurrency){}
 public record MobileWalletInitiationRequest(@NotBlank @Size(max=40)String providerCode,@NotBlank @Size(max=100)String idempotencyKey){}
 public record MobileWalletSessionResult(String paymentNumber,String attemptReference,String orderNumber,String providerCode,String providerDisplayName,BigDecimal amount,String currency,Instant expiresAt,String currentStep,String safeInstruction){}
 public record MobileWalletConfirmRequest(@NotBlank @Size(max=20)String walletNumber,@NotBlank @Size(max=20)String verificationCode,@NotBlank @Size(max=20)String paymentPin,@NotBlank @Size(max=100)String idempotencyKey){}}


