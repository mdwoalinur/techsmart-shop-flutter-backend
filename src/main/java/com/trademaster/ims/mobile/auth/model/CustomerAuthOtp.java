package com.trademaster.ims.mobile.auth.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_auth_otps", indexes={@Index(name="idx_customer_otp_account_purpose",columnList="customer_account_id,purpose")})
public class CustomerAuthOtp {
 public enum Purpose { REGISTRATION_VERIFY, PASSWORD_RESET }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="customer_account_id",nullable=false) private CustomerAccount account;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=32) private Purpose purpose;
 @Column(name="otp_hash",nullable=false,length=100) private String otpHash;
 @Column(name="expires_at",nullable=false) private Instant expiresAt;
 @Column(nullable=false) private int attempts;
 @Column(name="max_attempts",nullable=false) private int maxAttempts;
 @Column(name="resend_count",nullable=false) private int resendCount;
 @Column(name="last_sent_at",nullable=false) private Instant lastSentAt;
 @Column(name="consumed_at") private Instant consumedAt;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @PrePersist void create(){createdAt=Instant.now();}
 public Long getId(){return id;} public CustomerAccount getAccount(){return account;} public void setAccount(CustomerAccount v){account=v;} public Purpose getPurpose(){return purpose;} public void setPurpose(Purpose v){purpose=v;}
 public String getOtpHash(){return otpHash;} public void setOtpHash(String v){otpHash=v;} public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;} public int getAttempts(){return attempts;} public void setAttempts(int v){attempts=v;}
 public int getMaxAttempts(){return maxAttempts;} public void setMaxAttempts(int v){maxAttempts=v;} public int getResendCount(){return resendCount;} public void setResendCount(int v){resendCount=v;} public Instant getLastSentAt(){return lastSentAt;} public void setLastSentAt(Instant v){lastSentAt=v;}
 public Instant getConsumedAt(){return consumedAt;} public void setConsumedAt(Instant v){consumedAt=v;} public Instant getCreatedAt(){return createdAt;} public boolean active(){return consumedAt==null && expiresAt.isAfter(Instant.now());}
}
