package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="customer_payment_attempts", uniqueConstraints={@UniqueConstraint(name="uk_customer_attempt_idempotency", columnNames={"customer_account_id","order_id","idempotency_key"})}, indexes=@Index(name="idx_attempt_payment", columnList="payment_id"))
public class PaymentAttempt{
 public enum AttemptStatus{CREATED,SESSION_CREATED,PENDING,SUCCEEDED,FAILED,CANCELLED,EXPIRED,REVIEW_REQUIRED}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="payment_id",nullable=false)private Long paymentId;
 @Column(name="order_id",nullable=false)private Long orderId;
 @Column(name="customer_account_id",nullable=false)private Long accountId;
 @Column(nullable=false)private int attemptNumber;
 @Column(nullable=false,length=100)private String idempotencyKey;
 @Column(nullable=false,length=50)private String method;
 @Column(length=60)private String provider;
 @Column(nullable=false,precision=15,scale=2)private BigDecimal amount;
 @Column(nullable=false,length=10)private String currency="BDT";
 @Enumerated(EnumType.STRING)@Column(nullable=false,length=30)private AttemptStatus status=AttemptStatus.CREATED;
 @Column(length=150)private String gatewaySessionId;
 @Column(length=150)private String gatewayTransactionId;
 @Column(length=150)private String externalReference;
 @Column(nullable=false)private int failedCredentialAttempts=0;
 @Column private Instant expiresAt;
 @Column(nullable=false)private Instant createdAt;
 @Column(nullable=false)private Instant updatedAt;
 @Version private Long version;
 @PrePersist void p(){Instant n=Instant.now();createdAt=n;updatedAt=n;}@PreUpdate void u(){updatedAt=Instant.now();}
 public Long getId(){return id;} public Long getPaymentId(){return paymentId;} public void setPaymentId(Long v){paymentId=v;} public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;} public Long getAccountId(){return accountId;} public void setAccountId(Long v){accountId=v;} public int getAttemptNumber(){return attemptNumber;} public void setAttemptNumber(int v){attemptNumber=v;} public String getIdempotencyKey(){return idempotencyKey;} public void setIdempotencyKey(String v){idempotencyKey=v;} public String getMethod(){return method;} public void setMethod(String v){method=v;} public String getProvider(){return provider;} public void setProvider(String v){provider=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;} public AttemptStatus getStatus(){return status;} public void setStatus(AttemptStatus v){status=v;} public String getGatewaySessionId(){return gatewaySessionId;} public void setGatewaySessionId(String v){gatewaySessionId=v;} public String getGatewayTransactionId(){return gatewayTransactionId;} public void setGatewayTransactionId(String v){gatewayTransactionId=v;} public String getExternalReference(){return externalReference;} public void setExternalReference(String v){externalReference=v;} public int getFailedCredentialAttempts(){return failedCredentialAttempts;} public void setFailedCredentialAttempts(int v){failedCredentialAttempts=v;} public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public Long getVersion(){return version;}
}


