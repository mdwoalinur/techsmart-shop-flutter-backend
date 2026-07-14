package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_payment_status_history", indexes=@Index(name="idx_payment_status_history_payment", columnList="payment_id"))
public class PaymentStatusHistory{
 public enum ActorType{CUSTOMER,SYSTEM,ADMIN,GATEWAY,RECONCILIATION}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="payment_id",nullable=false)private Long paymentId;
 @Column(length=40)private String previousStatus;
 @Column(nullable=false,length=40)private String newStatus;
 @Column(nullable=false,length=40)private String source;
 @Column(length=500)private String customerVisibleNote;
 @Column(nullable=false)private Instant occurredAt;
 @Enumerated(EnumType.STRING)@Column(nullable=false,length=30)private ActorType actorType;
 @Column(length=120)private String actorReference;
 @PrePersist void p(){if(occurredAt==null)occurredAt=Instant.now();}
 public Long getId(){return id;} public Long getPaymentId(){return paymentId;} public void setPaymentId(Long v){paymentId=v;} public String getPreviousStatus(){return previousStatus;} public void setPreviousStatus(String v){previousStatus=v;} public String getNewStatus(){return newStatus;} public void setNewStatus(String v){newStatus=v;} public String getSource(){return source;} public void setSource(String v){source=v;} public String getCustomerVisibleNote(){return customerVisibleNote;} public void setCustomerVisibleNote(String v){customerVisibleNote=v;} public Instant getOccurredAt(){return occurredAt;} public void setOccurredAt(Instant v){occurredAt=v;} public ActorType getActorType(){return actorType;} public void setActorType(ActorType v){actorType=v;} public String getActorReference(){return actorReference;} public void setActorReference(String v){actorReference=v;}
}
