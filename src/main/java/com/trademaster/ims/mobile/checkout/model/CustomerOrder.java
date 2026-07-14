package com.trademaster.ims.mobile.checkout.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="customer_orders",uniqueConstraints={@UniqueConstraint(name="uk_order_number",columnNames="order_number"),@UniqueConstraint(name="uk_order_idempotency",columnNames={"customer_account_id","idempotency_key"})})
public class CustomerOrder{
 public enum OrderStatus{PENDING_CONFIRMATION,PENDING_PAYMENT,CONFIRMED,PROCESSING,PACKED,SHIPPED,OUT_FOR_DELIVERY,DELIVERED,CANCELLED,RETURN_REQUESTED,RETURNED,REFUNDED}
 public enum PaymentStatus{NOT_STARTED,INITIATED,PENDING_GATEWAY,VERIFIED,PAID,FAILED,CANCELLED,REVIEW_REQUIRED,PARTIALLY_REFUNDED,REFUNDED,CHARGEBACK,REVERSED,COD_PENDING,CASH_COLLECTED,RECONCILED}
 public enum AccountingStatus{UNPOSTED,POSTED,REVERSED}
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="order_number",nullable=false,length=40)private String orderNumber;
 @Column(name="customer_account_id",nullable=false)private Long accountId;
 @Column(name="idempotency_key",nullable=false,length=100)private String idempotencyKey;
 @Column(name="review_id",nullable=false,length=36)private String reviewId;
 @Enumerated(EnumType.STRING)@Column(name="order_status",nullable=false)private OrderStatus status=OrderStatus.PENDING_CONFIRMATION;
 @Enumerated(EnumType.STRING)@Column(name="payment_status",nullable=false)private PaymentStatus paymentStatus=PaymentStatus.NOT_STARTED;
 @Enumerated(EnumType.STRING)@Column(name="accounting_status",nullable=false)private AccountingStatus accountingStatus=AccountingStatus.UNPOSTED;
 @Column(nullable=false,precision=15,scale=2)private BigDecimal subtotal;
 @Column(name="tax_total",nullable=false,precision=15,scale=2)private BigDecimal tax;
 @Column(name="delivery_charge",nullable=false,precision=15,scale=2)private BigDecimal delivery;
 @Column(name="discount_total",nullable=false,precision=15,scale=2)private BigDecimal discount=BigDecimal.ZERO;
 @Column(name="grand_total",nullable=false,precision=15,scale=2)private BigDecimal total;
 @Column(name="recipient_name",nullable=false)private String recipient;
 @Column(nullable=false,length=20)private String phone;
 @Column(name="address_snapshot",nullable=false,length=1000)private String addressSnapshot;
 @Column(name="delivery_method_snapshot",nullable=false,length=250)private String deliverySnapshot;
 @Column(name="customer_note",length=500)private String note;
 @Column(name="submitted_at",nullable=false)private Instant submittedAt;
 @Column(name="updated_at")private Instant updatedAt;
 @Version private Long version;
 @PrePersist void prePersist(){if(updatedAt==null)updatedAt=Instant.now();}
 @PreUpdate void preUpdate(){updatedAt=Instant.now();}
 public Long getId(){return id;}public String getOrderNumber(){return orderNumber;}public void setOrderNumber(String v){orderNumber=v;}public Long getAccountId(){return accountId;}public void setAccountId(Long v){accountId=v;}public String getIdempotencyKey(){return idempotencyKey;}public void setIdempotencyKey(String v){idempotencyKey=v;}public String getReviewId(){return reviewId;}public void setReviewId(String v){reviewId=v;}public OrderStatus getStatus(){return status;}public void setStatus(OrderStatus v){status=v;}public PaymentStatus getPaymentStatus(){return paymentStatus;}public void setPaymentStatus(PaymentStatus v){paymentStatus=v;}public AccountingStatus getAccountingStatus(){return accountingStatus;}public void setAccountingStatus(AccountingStatus v){accountingStatus=v;}public BigDecimal getSubtotal(){return subtotal;}public void setSubtotal(BigDecimal v){subtotal=v;}public BigDecimal getTax(){return tax;}public void setTax(BigDecimal v){tax=v;}public BigDecimal getDelivery(){return delivery;}public void setDelivery(BigDecimal v){delivery=v;}public BigDecimal getDiscount(){return discount;}public BigDecimal getTotal(){return total;}public void setTotal(BigDecimal v){total=v;}public String getRecipient(){return recipient;}public void setRecipient(String v){recipient=v;}public String getPhone(){return phone;}public void setPhone(String v){phone=v;}public String getAddressSnapshot(){return addressSnapshot;}public void setAddressSnapshot(String v){addressSnapshot=v;}public String getDeliverySnapshot(){return deliverySnapshot;}public void setDeliverySnapshot(String v){deliverySnapshot=v;}public String getNote(){return note;}public void setNote(String v){note=v;}public Instant getSubmittedAt(){return submittedAt;}public void setSubmittedAt(Instant v){submittedAt=v;}public Instant getUpdatedAt(){return updatedAt;}public Long getVersion(){return version;}
}

