package com.trademaster.ims.mobile.auth.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_reset_authorizations",indexes=@Index(name="idx_customer_reset_hash",columnList="token_hash",unique=true))
public class CustomerResetAuthorization {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="customer_account_id",nullable=false) private CustomerAccount account;
 @Column(name="token_hash",nullable=false,unique=true,length=64) private String tokenHash;
 @Column(name="expires_at",nullable=false) private Instant expiresAt;
 @Column(name="consumed_at") private Instant consumedAt;
 @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
 public Long getId(){return id;} public CustomerAccount getAccount(){return account;} public void setAccount(CustomerAccount v){account=v;} public String getTokenHash(){return tokenHash;} public void setTokenHash(String v){tokenHash=v;} public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;} public Instant getConsumedAt(){return consumedAt;} public void setConsumedAt(Instant v){consumedAt=v;} public Instant getCreatedAt(){return createdAt;}
}
