package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="customer_payment_reconciliation_records", uniqueConstraints=@UniqueConstraint(name="uk_payment_settlement_reference", columnNames={"provider","settlement_reference"}))
public class PaymentReconciliationRecord{
 public enum ReconciliationStatus{PENDING,MATCHED,MISMATCH,RECONCILED,REVIEW_REQUIRED}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="payment_id",nullable=false)private Long paymentId;
 @Column(nullable=false,length=60)private String provider;
 @Column(name="settlement_reference",nullable=false,length=150)private String settlementReference;
 @Column(nullable=false,precision=15,scale=2)private BigDecimal expectedAmount;
 @Column(nullable=false,precision=15,scale=2)private BigDecimal settledAmount;
 @Enumerated(EnumType.STRING)@Column(nullable=false,length=30)private ReconciliationStatus status=ReconciliationStatus.PENDING;
 @Column(length=500)private String mismatchReason;
 @Column private Instant reconciledAt;
 @Column(nullable=false)private Instant createdAt;
 @PrePersist void p(){createdAt=Instant.now();}
 public Long getId(){return id;} public Long getPaymentId(){return paymentId;} public void setPaymentId(Long v){paymentId=v;} public String getProvider(){return provider;} public void setProvider(String v){provider=v;} public String getSettlementReference(){return settlementReference;} public void setSettlementReference(String v){settlementReference=v;} public BigDecimal getExpectedAmount(){return expectedAmount;} public void setExpectedAmount(BigDecimal v){expectedAmount=v;} public BigDecimal getSettledAmount(){return settledAmount;} public void setSettledAmount(BigDecimal v){settledAmount=v;} public ReconciliationStatus getStatus(){return status;} public void setStatus(ReconciliationStatus v){status=v;} public String getMismatchReason(){return mismatchReason;} public void setMismatchReason(String v){mismatchReason=v;} public Instant getReconciledAt(){return reconciledAt;} public void setReconciledAt(Instant v){reconciledAt=v;} public Instant getCreatedAt(){return createdAt;}
}
