package com.trademaster.ims.mobile.offers.model;

import jakarta.persistence.*;
import java.math.*;
import java.time.*;

@Entity
@Table(name="mobile_offer_products", uniqueConstraints=@UniqueConstraint(name="uk_offer_product", columnNames={"offer_id","product_id"}), indexes={@Index(name="idx_offer_product_product", columnList="product_id"),@Index(name="idx_offer_product_offer", columnList="offer_id")})
public class MobileOfferProduct {
 public enum DiscountType { PERCENT, FIXED_AMOUNT, PRICE_OVERRIDE }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="offer_id", nullable=false) private Long offerId;
 @Column(name="product_id", nullable=false) private Long productId;
 @Enumerated(EnumType.STRING) @Column(name="discount_type", nullable=false, length=24) private DiscountType discountType=DiscountType.PERCENT;
 @Column(name="discount_value", nullable=false, precision=15, scale=2) private BigDecimal discountValue=BigDecimal.ZERO;
 @Column(nullable=false) private boolean active=true;
 @Column(name="display_order") private Integer displayOrder=0;
 @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
 @Column(name="updated_at", nullable=false) private Instant updatedAt;
 @PrePersist void p(){Instant n=Instant.now();createdAt=n;updatedAt=n;} @PreUpdate void u(){updatedAt=Instant.now();}
 public Long getId(){return id;} public Long getOfferId(){return offerId;} public void setOfferId(Long v){offerId=v;} public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;} public DiscountType getDiscountType(){return discountType;} public void setDiscountType(DiscountType v){discountType=v;} public BigDecimal getDiscountValue(){return discountValue;} public void setDiscountValue(BigDecimal v){discountValue=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public Integer getDisplayOrder(){return displayOrder;} public void setDisplayOrder(Integer v){displayOrder=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}