package com.trademaster.ims.mobile.auth.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_refresh_tokens", indexes={@Index(name="idx_customer_refresh_hash",columnList="token_hash",unique=true),@Index(name="idx_customer_refresh_family",columnList="token_family_id")})
public class CustomerRefreshToken {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="customer_account_id",nullable=false) private CustomerAccount account;
 @Column(name="token_hash",nullable=false,unique=true,length=64) private String tokenHash;
 @Column(name="token_family_id",nullable=false,length=36) private String tokenFamilyId;
 @Column(name="issued_at",nullable=false) private Instant issuedAt;
 @Column(name="expires_at",nullable=false) private Instant expiresAt;
 @Column(name="revoked_at") private Instant revokedAt;
 @Column(name="replaced_by_token_id") private Long replacedByTokenId;
 @Column(name="device_description",length=200) private String deviceDescription;
 public Long getId(){return id;} public CustomerAccount getAccount(){return account;} public void setAccount(CustomerAccount v){account=v;} public String getTokenHash(){return tokenHash;} public void setTokenHash(String v){tokenHash=v;}
 public String getTokenFamilyId(){return tokenFamilyId;} public void setTokenFamilyId(String v){tokenFamilyId=v;} public Instant getIssuedAt(){return issuedAt;} public void setIssuedAt(Instant v){issuedAt=v;} public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;}
 public Instant getRevokedAt(){return revokedAt;} public void setRevokedAt(Instant v){revokedAt=v;} public Long getReplacedByTokenId(){return replacedByTokenId;} public void setReplacedByTokenId(Long v){replacedByTokenId=v;} public String getDeviceDescription(){return deviceDescription;} public void setDeviceDescription(String v){deviceDescription=v;}
}
