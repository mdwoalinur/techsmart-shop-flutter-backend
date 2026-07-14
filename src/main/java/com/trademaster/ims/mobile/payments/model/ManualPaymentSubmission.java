package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="manual_payment_submissions", uniqueConstraints=@UniqueConstraint(name="uk_manual_payment_reference", columnNames={"method","transaction_reference"}), indexes=@Index(name="idx_manual_submission_payment", columnList="payment_id"))
public class ManualPaymentSubmission{
 public enum SubmissionStatus{SUBMITTED,PENDING_REVIEW,APPROVED,REJECTED}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="payment_id",nullable=false)private Long paymentId;
 @Column(nullable=false,length=50)private String method;
 @Column(name="transaction_reference",nullable=false,length=150)private String transactionReference;
 @Column(length=120)private String payerName;
 @Column(length=30)private String payerPhone;
 @Column(nullable=false,precision=15,scale=2)private BigDecimal submittedAmount;
 @Column(nullable=false)private Instant submittedAt;
 @Enumerated(EnumType.STRING)@Column(nullable=false,length=30)private SubmissionStatus status=SubmissionStatus.PENDING_REVIEW;
 @Column(length=500)private String customerNote;
 @Column(length=500)private String reviewNote;
 @Version private Long version;
 @PrePersist void p(){if(submittedAt==null)submittedAt=Instant.now();}
 public Long getId(){return id;} public Long getPaymentId(){return paymentId;} public void setPaymentId(Long v){paymentId=v;} public String getMethod(){return method;} public void setMethod(String v){method=v;} public String getTransactionReference(){return transactionReference;} public void setTransactionReference(String v){transactionReference=v;} public String getPayerName(){return payerName;} public void setPayerName(String v){payerName=v;} public String getPayerPhone(){return payerPhone;} public void setPayerPhone(String v){payerPhone=v;} public BigDecimal getSubmittedAmount(){return submittedAmount;} public void setSubmittedAmount(BigDecimal v){submittedAmount=v;} public Instant getSubmittedAt(){return submittedAt;} public void setSubmittedAt(Instant v){submittedAt=v;} public SubmissionStatus getStatus(){return status;} public void setStatus(SubmissionStatus v){status=v;} public String getCustomerNote(){return customerNote;} public void setCustomerNote(String v){customerNote=v;} public String getReviewNote(){return reviewNote;} public void setReviewNote(String v){reviewNote=v;} public Long getVersion(){return version;}
}
