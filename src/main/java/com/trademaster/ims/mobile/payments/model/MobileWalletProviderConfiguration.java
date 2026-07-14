package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="mobile_wallet_provider_configurations", uniqueConstraints=@UniqueConstraint(name="uk_mobile_wallet_provider_code", columnNames="code"))
public class MobileWalletProviderConfiguration{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(nullable=false,length=40)private String code;
 @Column(nullable=false,length=120)private String displayName;
 @Column(length=500)private String shortDescription;
 @Column(nullable=false)private boolean active=true;
 @Column(nullable=false)private int displayOrder=100;
 @Column(nullable=false)private boolean simulationEnabled=true;
 @Column(length=80)private String iconAssetKey;
 @Column(length=80)private String visualThemeKey;
 @Column(length=80)private String phoneLabel="Wallet number";
 @Column(length=80)private String phoneHint="01XXXXXXXXX";
 @Column(nullable=false)private boolean requiresVerificationCode=true;
 @Column(nullable=false)private boolean requiresPaymentPin=true;
 @Column(length=1000)private String instructions;
 @Column(precision=15,scale=2)private BigDecimal minAmount;
 @Column(precision=15,scale=2)private BigDecimal maxAmount;
 @Column(nullable=false,length=10)private String supportedCurrency="BDT";
 @Column(nullable=false)private Instant createdAt;
 @Column(nullable=false)private Instant updatedAt;
 @PrePersist void p(){Instant n=Instant.now();createdAt=n;updatedAt=n;}@PreUpdate void u(){updatedAt=Instant.now();}
 public Long getId(){return id;} public String getCode(){return code;} public void setCode(String v){code=v;} public String getDisplayName(){return displayName;} public void setDisplayName(String v){displayName=v;} public String getShortDescription(){return shortDescription;} public void setShortDescription(String v){shortDescription=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public int getDisplayOrder(){return displayOrder;} public void setDisplayOrder(int v){displayOrder=v;} public boolean isSimulationEnabled(){return simulationEnabled;} public void setSimulationEnabled(boolean v){simulationEnabled=v;} public String getIconAssetKey(){return iconAssetKey;} public void setIconAssetKey(String v){iconAssetKey=v;} public String getVisualThemeKey(){return visualThemeKey;} public void setVisualThemeKey(String v){visualThemeKey=v;} public String getPhoneLabel(){return phoneLabel;} public void setPhoneLabel(String v){phoneLabel=v;} public String getPhoneHint(){return phoneHint;} public void setPhoneHint(String v){phoneHint=v;} public boolean isRequiresVerificationCode(){return requiresVerificationCode;} public void setRequiresVerificationCode(boolean v){requiresVerificationCode=v;} public boolean isRequiresPaymentPin(){return requiresPaymentPin;} public void setRequiresPaymentPin(boolean v){requiresPaymentPin=v;} public String getInstructions(){return instructions;} public void setInstructions(String v){instructions=v;} public BigDecimal getMinAmount(){return minAmount;} public void setMinAmount(BigDecimal v){minAmount=v;} public BigDecimal getMaxAmount(){return maxAmount;} public void setMaxAmount(BigDecimal v){maxAmount=v;} public String getSupportedCurrency(){return supportedCurrency;} public void setSupportedCurrency(String v){supportedCurrency=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
