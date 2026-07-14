package com.trademaster.ims.mobile.payments.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "customer_payments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_payment_number", columnNames = "payment_number"),
        @UniqueConstraint(name = "uk_customer_gateway_tx", columnNames = "gateway_transaction_id"),
        @UniqueConstraint(name = "uk_customer_payment_posting", columnNames = "posting_key")
}, indexes = {@Index(name = "idx_customer_payment_order", columnList = "order_id"), @Index(name = "idx_customer_payment_account", columnList = "customer_account_id")})
public class CustomerPayment {
    public enum PaymentStatus { NOT_STARTED, INITIATED, PENDING_GATEWAY, VERIFIED, PAID, FAILED, CANCELLED, REVIEW_REQUIRED, PARTIALLY_REFUNDED, REFUNDED, CHARGEBACK, REVERSED, COD_PENDING, CASH_COLLECTED, RECONCILED }
    public enum AccountingStatus { UNPOSTED, POSTED, REVERSED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="payment_number", nullable=false, length=50) private String paymentNumber;
    @Column(name="order_id", nullable=false) private Long orderId;
    @Column(name="order_number", nullable=false, length=50) private String orderNumber;
    @Column(name="customer_account_id", nullable=false) private Long accountId;
    @Column(nullable=false, length=50) private String methodCode;
    @Column(nullable=false, length=40) private String methodType;
    @Column(nullable=false, precision=15, scale=2) private BigDecimal amount;
    @Column(nullable=false, length=10) private String currency = "BDT";
    @Enumerated(EnumType.STRING) @Column(name="payment_status", nullable=false, length=40) private PaymentStatus paymentStatus = PaymentStatus.NOT_STARTED;
    @Enumerated(EnumType.STRING) @Column(name="accounting_status", nullable=false, length=30) private AccountingStatus accountingStatus = AccountingStatus.UNPOSTED;
    @Column(length=60) private String gatewayProvider;
    @Column(name="gateway_transaction_id", length=150) private String gatewayTransactionId;
    @Column private Instant paidAt;
    @Column private Instant verifiedAt;
    @Column private Instant postedAt;
    @Column(length=80) private String failureCode;
    @Column(length=500) private String customerVisibleMessage;
    @Column(name="posting_key", length=120) private String postingKey;
    @Column private Long ledgerEntryId;
    @Column(nullable=false) private Instant createdAt;
    @Column(nullable=false) private Instant updatedAt;
    @Version private Long version;
    @PrePersist void prePersist(){Instant n=Instant.now();createdAt=n;updatedAt=n;}
    @PreUpdate void preUpdate(){updatedAt=Instant.now();}
    public Long getId(){return id;} public String getPaymentNumber(){return paymentNumber;} public void setPaymentNumber(String v){paymentNumber=v;} public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;} public String getOrderNumber(){return orderNumber;} public void setOrderNumber(String v){orderNumber=v;} public Long getAccountId(){return accountId;} public void setAccountId(Long v){accountId=v;} public String getMethodCode(){return methodCode;} public void setMethodCode(String v){methodCode=v;} public String getMethodType(){return methodType;} public void setMethodType(String v){methodType=v;} public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;} public PaymentStatus getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(PaymentStatus v){paymentStatus=v;} public AccountingStatus getAccountingStatus(){return accountingStatus;} public void setAccountingStatus(AccountingStatus v){accountingStatus=v;} public String getGatewayProvider(){return gatewayProvider;} public void setGatewayProvider(String v){gatewayProvider=v;} public String getGatewayTransactionId(){return gatewayTransactionId;} public void setGatewayTransactionId(String v){gatewayTransactionId=v;} public Instant getPaidAt(){return paidAt;} public void setPaidAt(Instant v){paidAt=v;} public Instant getVerifiedAt(){return verifiedAt;} public void setVerifiedAt(Instant v){verifiedAt=v;} public Instant getPostedAt(){return postedAt;} public void setPostedAt(Instant v){postedAt=v;} public String getFailureCode(){return failureCode;} public void setFailureCode(String v){failureCode=v;} public String getCustomerVisibleMessage(){return customerVisibleMessage;} public void setCustomerVisibleMessage(String v){customerVisibleMessage=v;} public String getPostingKey(){return postingKey;} public void setPostingKey(String v){postingKey=v;} public Long getLedgerEntryId(){return ledgerEntryId;} public void setLedgerEntryId(Long v){ledgerEntryId=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public Long getVersion(){return version;}
}
