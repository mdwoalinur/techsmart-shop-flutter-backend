package com.trademaster.ims.mobile.shopping.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="customer_shopping_merge_receipts",uniqueConstraints=@UniqueConstraint(name="uk_shopping_merge_request",columnNames={"customer_account_id","merge_type","request_id"}))
public class ShoppingMergeReceipt {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="customer_account_id",nullable=false) private Long customerAccountId; @Column(name="merge_type",nullable=false,length=16) private String mergeType; @Column(name="request_id",nullable=false,length=100) private String requestId; @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @PrePersist void create(){createdAt=Instant.now();} public void setCustomerAccountId(Long v){customerAccountId=v;} public void setMergeType(String v){mergeType=v;} public void setRequestId(String v){requestId=v;}
}
