package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="customer_payment_refunds", uniqueConstraints={@UniqueConstraint(name="uk_refund_idempotency", columnNames={"payment_id","idempotency_key"}), @UniqueConstraint(name="uk_provider_refund_id", columnNames="provider_refund_id")})
public class PaymentRefund{
 public enum RefundStatus{REFUND_REQUESTED,REFUND_PENDING,PARTIALLY_REFUNDED,REFUNDED,FAILED}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="payment_id",nullable=false)private Long paymentId;
 @Column(nullable=false,length=100)private String idempotencyKey;
 @Column(nullable=false,precision=15,scale=2)private BigDecimal originalAmount;
 @Column(nullable=false,precision=15,scale=2)private BigDecimal requestedAmount;
 @Column(precision=15,scale=2)private BigDecimal processedAmount;
 @Enumerated(EnumType.STRING)@Column(nullable=false,length=30)private RefundStatus status=RefundStatus.REFUND_REQUESTED;
 @Column(name="provider_refund_id",length=150)private String providerRefundId;
 @Column(nullable=false,length=500)private String reason;
 @Column(nullable=false)private Instant createdAt;
 @Column private Instant processedAt;
 @PrePersist void p(){createdAt=Instant.now();}
 public Long getId(){return id;} public Long getPaymentId(){return paymentId;} public void setPaymentId(Long v){paymentId=v;} public String getIdempotencyKey(){return idempotencyKey;} public void setIdempotencyKey(String v){idempotencyKey=v;} public BigDecimal getOriginalAmount(){return originalAmount;} public void setOriginalAmount(BigDecimal v){originalAmount=v;} public BigDecimal getRequestedAmount(){return requestedAmount;} public void setRequestedAmount(BigDecimal v){requestedAmount=v;} public BigDecimal getProcessedAmount(){return processedAmount;} public void setProcessedAmount(BigDecimal v){processedAmount=v;} public RefundStatus getStatus(){return status;} public void setStatus(RefundStatus v){status=v;} public String getProviderRefundId(){return providerRefundId;} public void setProviderRefundId(String v){providerRefundId=v;} public String getReason(){return reason;} public void setReason(String v){reason=v;} public Instant getCreatedAt(){return createdAt;} public Instant getProcessedAt(){return processedAt;} public void setProcessedAt(Instant v){processedAt=v;}
}
