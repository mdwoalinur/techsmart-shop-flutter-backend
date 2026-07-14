package com.trademaster.ims.mobile.auth.model;

import com.trademaster.ims.model.Customer;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_accounts", indexes={@Index(name="idx_customer_account_email",columnList="email",unique=true),@Index(name="idx_customer_account_status",columnList="status")})
public class CustomerAccount {
 public enum Status { PENDING_VERIFICATION, ACTIVE, LOCKED, DISABLED }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @OneToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false,unique=true) private Customer customer;
 @Column(nullable=false,unique=true,length=254) private String email;
 @Column(name="password_hash",nullable=false,length=100) private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=32) private Status status=Status.PENDING_VERIFICATION;
 @Column(name="email_verified",nullable=false) private boolean emailVerified;
 @Column(name="failed_login_attempts",nullable=false) private int failedLoginAttempts;
 @Column(name="locked_until") private Instant lockedUntil;
 @Column(name="last_login_at") private Instant lastLoginAt;
 @Column(name="password_changed_at") private Instant passwordChangedAt;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @PrePersist void create(){createdAt=updatedAt=Instant.now();}
 @PreUpdate void update(){updatedAt=Instant.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public Customer getCustomer(){return customer;} public void setCustomer(Customer v){customer=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
 public Status getStatus(){return status;} public void setStatus(Status v){status=v;} public boolean isEmailVerified(){return emailVerified;} public void setEmailVerified(boolean v){emailVerified=v;}
 public int getFailedLoginAttempts(){return failedLoginAttempts;} public void setFailedLoginAttempts(int v){failedLoginAttempts=v;} public Instant getLockedUntil(){return lockedUntil;} public void setLockedUntil(Instant v){lockedUntil=v;}
 public Instant getLastLoginAt(){return lastLoginAt;} public void setLastLoginAt(Instant v){lastLoginAt=v;} public Instant getPasswordChangedAt(){return passwordChangedAt;} public void setPasswordChangedAt(Instant v){passwordChangedAt=v;}
 public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
