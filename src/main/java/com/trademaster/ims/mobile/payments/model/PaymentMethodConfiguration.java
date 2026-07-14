package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "mobile_payment_method_configurations", uniqueConstraints = @UniqueConstraint(name = "uk_mobile_payment_method_code", columnNames = "code"))
public class PaymentMethodConfiguration {
    public enum MethodType { ONLINE_GATEWAY, MOBILE_WALLET, BANK_TRANSFER, MOBILE_FINANCIAL_SERVICE, CASH_ON_DELIVERY }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 50) private String code;
    @Column(nullable = false, length = 120) private String displayName;
    @Column(length = 500) private String description;
    @Column(nullable = false) private boolean active = true;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private MethodType type;
    @Column(length = 60) private String provider;
    @Column(precision = 15, scale = 2) private BigDecimal minAmount;
    @Column(precision = 15, scale = 2) private BigDecimal maxAmount;
    @Column(nullable = false, length = 10) private String supportedCurrency = "BDT";
    @Column(length = 1000) private String customerInstructions;
    @Column(nullable = false) private boolean requiresReference;
    @Column(nullable = false) private boolean requiresProof;
    @Column(nullable = false) private boolean autoVerify;
    @Column(nullable = false) private boolean reviewRequired;
    @Column(nullable = false) private boolean codEligible = true;
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
    @PrePersist void prePersist(){Instant n=Instant.now();createdAt=n;updatedAt=n;}
    @PreUpdate void preUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public String getCode(){return code;} public void setCode(String v){code=v;} public String getDisplayName(){return displayName;} public void setDisplayName(String v){displayName=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public MethodType getType(){return type;} public void setType(MethodType v){type=v;} public String getProvider(){return provider;} public void setProvider(String v){provider=v;} public BigDecimal getMinAmount(){return minAmount;} public void setMinAmount(BigDecimal v){minAmount=v;} public BigDecimal getMaxAmount(){return maxAmount;} public void setMaxAmount(BigDecimal v){maxAmount=v;} public String getSupportedCurrency(){return supportedCurrency;} public void setSupportedCurrency(String v){supportedCurrency=v;} public String getCustomerInstructions(){return customerInstructions;} public void setCustomerInstructions(String v){customerInstructions=v;} public boolean isRequiresReference(){return requiresReference;} public void setRequiresReference(boolean v){requiresReference=v;} public boolean isRequiresProof(){return requiresProof;} public void setRequiresProof(boolean v){requiresProof=v;} public boolean isAutoVerify(){return autoVerify;} public void setAutoVerify(boolean v){autoVerify=v;} public boolean isReviewRequired(){return reviewRequired;} public void setReviewRequired(boolean v){reviewRequired=v;} public boolean isCodEligible(){return codEligible;} public void setCodEligible(boolean v){codEligible=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}

