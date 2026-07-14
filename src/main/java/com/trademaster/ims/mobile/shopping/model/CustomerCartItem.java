package com.trademaster.ims.mobile.shopping.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="customer_cart_items",uniqueConstraints=@UniqueConstraint(name="uk_cart_product_variation",columnNames={"cart_id","product_id","variation_key"}))
public class CustomerCartItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="cart_id",nullable=false) private Long cartId;
 @Column(name="product_id",nullable=false) private Long productId;
 @Column(name="product_variation_id") private Long variationId;
 @Column(name="variation_key",nullable=false) private Long variationKey=0L;
 @Column(nullable=false) private int quantity;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Version private long version;
 @PrePersist void create(){createdAt=updatedAt=Instant.now(); normalize();} @PreUpdate void update(){updatedAt=Instant.now();normalize();} private void normalize(){variationKey=variationId==null?0L:variationId;}
 public Long getId(){return id;} public Long getCartId(){return cartId;} public void setCartId(Long v){cartId=v;} public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
 public Long getVariationId(){return variationId;} public void setVariationId(Long v){variationId=v;normalize();} public Long getVariationKey(){return variationKey;} public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;}
}
