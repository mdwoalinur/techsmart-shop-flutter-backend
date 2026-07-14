package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_payment_events", uniqueConstraints=@UniqueConstraint(name="uk_customer_gateway_event", columnNames={"provider","gateway_event_id"}), indexes=@Index(name="idx_payment_event_payment", columnList="payment_id"))
public class PaymentEvent{
 public enum ProcessingStatus{RECEIVED,PROCESSED,DUPLICATE,FAILED,REVIEW_REQUIRED}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="payment_id")private Long paymentId;
 @Column(nullable=false,length=60)private String provider;
 @Column(name="gateway_event_id",nullable=false,length=150)private String gatewayEventId;
 @Column(nullable=false,length=80)private String eventType;
 @Column(nullable=false)private boolean signatureValid;
 @Column(nullable=false,length=80)private String payloadHash;
 @Enumerated(EnumType.STRING)@Column(nullable=false,length=30)private ProcessingStatus processingStatus=ProcessingStatus.RECEIVED;
 @Column private Instant processedAt;
 @Column(length=500)private String failureReason;
 @Column(nullable=false)private Instant createdAt;
 @PrePersist void p(){createdAt=Instant.now();}
 public Long getId(){return id;} public Long getPaymentId(){return paymentId;} public void setPaymentId(Long v){paymentId=v;} public String getProvider(){return provider;} public void setProvider(String v){provider=v;} public String getGatewayEventId(){return gatewayEventId;} public void setGatewayEventId(String v){gatewayEventId=v;} public String getEventType(){return eventType;} public void setEventType(String v){eventType=v;} public boolean isSignatureValid(){return signatureValid;} public void setSignatureValid(boolean v){signatureValid=v;} public String getPayloadHash(){return payloadHash;} public void setPayloadHash(String v){payloadHash=v;} public ProcessingStatus getProcessingStatus(){return processingStatus;} public void setProcessingStatus(ProcessingStatus v){processingStatus=v;} public Instant getProcessedAt(){return processedAt;} public void setProcessedAt(Instant v){processedAt=v;} public String getFailureReason(){return failureReason;} public void setFailureReason(String v){failureReason=v;} public Instant getCreatedAt(){return createdAt;}
}
