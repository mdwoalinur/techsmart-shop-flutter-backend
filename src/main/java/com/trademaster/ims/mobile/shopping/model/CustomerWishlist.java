package com.trademaster.ims.mobile.shopping.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="customer_wishlists",uniqueConstraints=@UniqueConstraint(name="uk_customer_wishlist_account",columnNames="customer_account_id"))
public class CustomerWishlist {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="customer_account_id",nullable=false) private Long customerAccountId;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt; @Column(name="updated_at",nullable=false) private Instant updatedAt; @Version private long version;
 @PrePersist void create(){createdAt=updatedAt=Instant.now();} @PreUpdate void update(){updatedAt=Instant.now();}
 public Long getId(){return id;} public Long getCustomerAccountId(){return customerAccountId;} public void setCustomerAccountId(Long v){customerAccountId=v;} public Instant getUpdatedAt(){return updatedAt;}
}
