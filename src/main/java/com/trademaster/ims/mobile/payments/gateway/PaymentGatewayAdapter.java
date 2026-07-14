package com.trademaster.ims.mobile.payments.gateway;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public interface PaymentGatewayAdapter {
 boolean supports(String providerCode);
 GatewaySession createSession(GatewaySessionRequest request);
 VerificationResult verifyCallback(String rawBody,String signature);
 TransactionQueryResult queryTransaction(String paymentNumber);
 record GatewaySessionRequest(String paymentNumber,String orderNumber,BigDecimal amount,String currency,int attemptNumber,String returnTarget){}
 record GatewaySession(String provider,String sessionId,String redirectUrl,Instant expiresAt){}
 record VerificationResult(boolean signatureValid,String eventId,String eventType,String paymentNumber,String orderNumber,int attemptNumber,String transactionId,BigDecimal amount,String currency,String status,String failureReason,Map<String,Object> safeFields){}
 record TransactionQueryResult(String provider,String paymentNumber,String transactionId,BigDecimal amount,String currency,String status){}
}
