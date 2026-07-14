package com.trademaster.ims.mobile.shopping.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="customer_carts", uniqueConstraints=@UniqueConstraint(name="uk_customer_cart_account_status",columnNames={"customer_account_id","status"}))
public class CustomerCart {
 public enum Status { ACTIVE }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="customer_account_id",nullable=false) private Long customerAccountId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private Status status=Status.ACTIVE;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Version private long version;
 @PrePersist void create(){createdAt=updatedAt=Instant.now();} @PreUpdate void update(){updatedAt=Instant.now();}
 public Long getId(){return id;} public Long getCustomerAccountId(){return customerAccountId;} public void setCustomerAccountId(Long v){customerAccountId=v;}
 public Status getStatus(){return status;} public Instant getUpdatedAt(){return updatedAt;} public long getVersion(){return version;}
}
