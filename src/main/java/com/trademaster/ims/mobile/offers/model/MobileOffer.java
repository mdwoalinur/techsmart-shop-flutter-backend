package com.trademaster.ims.mobile.offers.model;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name="mobile_offers", indexes={@Index(name="idx_offer_active_visible", columnList="active,visible,start_at,end_at")})
public class MobileOffer {
 public enum OfferStatus { DRAFT, ACTIVE, PAUSED, EXPIRED }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true, length=80) private String code;
 @Column(nullable=false, length=160) private String title;
 @Column(length=240) private String subtitle;
 @Column(length=900) private String description;
 @Column(name="banner_url", length=500) private String bannerUrl;
 @Column(length=60) private String channel="Online";
 @Column(nullable=false) private boolean active=true;
 @Column(nullable=false) private boolean visible=true;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=24) private OfferStatus status=OfferStatus.ACTIVE;
 @Column(name="start_at", nullable=false) private Instant startAt;
 @Column(name="end_at", nullable=false) private Instant endAt;
 @Column(name="display_order") private Integer displayOrder=0;
 @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt;
 @Column(name="updated_at", nullable=false) private Instant updatedAt;
 @PrePersist void p(){Instant n=Instant.now();createdAt=n;updatedAt=n;if(startAt==null)startAt=n;if(endAt==null)endAt=n.plus(Duration.ofDays(30));}
 @PreUpdate void u(){updatedAt=Instant.now();}
 public Long getId(){return id;} public String getCode(){return code;} public void setCode(String v){code=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getSubtitle(){return subtitle;} public void setSubtitle(String v){subtitle=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getBannerUrl(){return bannerUrl;} public void setBannerUrl(String v){bannerUrl=v;} public String getChannel(){return channel;} public void setChannel(String v){channel=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public boolean isVisible(){return visible;} public void setVisible(boolean v){visible=v;} public OfferStatus getStatus(){return status;} public void setStatus(OfferStatus v){status=v;} public Instant getStartAt(){return startAt;} public void setStartAt(Instant v){startAt=v;} public Instant getEndAt(){return endAt;} public void setEndAt(Instant v){endAt=v;} public Integer getDisplayOrder(){return displayOrder;} public void setDisplayOrder(Integer v){displayOrder=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}