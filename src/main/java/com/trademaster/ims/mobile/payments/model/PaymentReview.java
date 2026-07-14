package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_payment_reviews", indexes=@Index(name="idx_payment_review_payment", columnList="payment_id"))
public class PaymentReview{
 public enum ReviewStatus{PENDING_REVIEW,APPROVED,REJECTED,CANCELLED}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="payment_id",nullable=false)private Long paymentId;
 @Column(nullable=false,length=80)private String reasonCode;
 @Enumerated(EnumType.STRING)@Column(nullable=false,length=30)private ReviewStatus status=ReviewStatus.PENDING_REVIEW;
 @Column(length=120)private String assignedReviewer;
 @Column(length=500)private String resolution;
 @Column(length=500)private String customerVisibleNote;
 @Column private Instant reviewedAt;
 @Column(nullable=false)private Instant createdAt;
 @Column(nullable=false)private Instant updatedAt;
 @Version private Long version;
 @PrePersist void p(){Instant n=Instant.now();createdAt=n;updatedAt=n;}@PreUpdate void u(){updatedAt=Instant.now();}
 public Long getId(){return id;} public Long getPaymentId(){return paymentId;} public void setPaymentId(Long v){paymentId=v;} public String getReasonCode(){return reasonCode;} public void setReasonCode(String v){reasonCode=v;} public ReviewStatus getStatus(){return status;} public void setStatus(ReviewStatus v){status=v;} public String getAssignedReviewer(){return assignedReviewer;} public void setAssignedReviewer(String v){assignedReviewer=v;} public String getResolution(){return resolution;} public void setResolution(String v){resolution=v;} public String getCustomerVisibleNote(){return customerVisibleNote;} public void setCustomerVisibleNote(String v){customerVisibleNote=v;} public Instant getReviewedAt(){return reviewedAt;} public void setReviewedAt(Instant v){reviewedAt=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public Long getVersion(){return version;}
}
