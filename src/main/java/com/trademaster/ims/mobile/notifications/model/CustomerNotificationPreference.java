package com.trademaster.ims.mobile.notifications.model;

import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_notification_preferences", uniqueConstraints=@UniqueConstraint(name="uk_cust_notif_pref_customer_category",columnNames={"customer_account_id","category"}), indexes=@Index(name="idx_cust_notif_pref_customer",columnList="customer_account_id"))
public class CustomerNotificationPreference {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="customer_account_id",nullable=false) private CustomerAccount customer;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=24) private CustomerNotification.Category category;
 @Column(name="in_app_enabled",nullable=false) private boolean inAppEnabled=true;
 @Column(name="email_enabled",nullable=false) private boolean emailEnabled=true;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @PrePersist void create(){createdAt=updatedAt=Instant.now();}
 @PreUpdate void update(){updatedAt=Instant.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public CustomerAccount getCustomer(){return customer;} public void setCustomer(CustomerAccount v){customer=v;} public CustomerNotification.Category getCategory(){return category;} public void setCategory(CustomerNotification.Category v){category=v;} public boolean isInAppEnabled(){return inAppEnabled;} public void setInAppEnabled(boolean v){inAppEnabled=v;} public boolean isEmailEnabled(){return emailEnabled;} public void setEmailEnabled(boolean v){emailEnabled=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}