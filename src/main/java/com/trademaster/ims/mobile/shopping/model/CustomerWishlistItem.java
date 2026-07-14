package com.trademaster.ims.mobile.shopping.model;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="customer_wishlist_items",uniqueConstraints=@UniqueConstraint(name="uk_wishlist_product",columnNames={"wishlist_id","product_id"}))
public class CustomerWishlistItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(name="wishlist_id",nullable=false) private Long wishlistId; @Column(name="product_id",nullable=false) private Long productId; @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @PrePersist void create(){createdAt=Instant.now();} public Long getId(){return id;} public Long getWishlistId(){return wishlistId;} public void setWishlistId(Long v){wishlistId=v;} public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
}
