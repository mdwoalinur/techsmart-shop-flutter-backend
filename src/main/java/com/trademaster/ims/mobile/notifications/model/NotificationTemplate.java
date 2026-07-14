package com.trademaster.ims.mobile.notifications.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_notification_templates", indexes={@Index(name="idx_cust_notif_tpl_code_locale",columnList="code,locale",unique=true)})
public class NotificationTemplate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=80) private String code;
 @Column(nullable=false,length=160) private String titleTemplate;
 @Column(nullable=false,columnDefinition="TEXT") private String messageTemplate;
 @Column(nullable=false,length=240) private String shortMessageTemplate;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=24) private CustomerNotification.Category category;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private CustomerNotification.Severity severity;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private CustomerNotification.Channel defaultChannel=CustomerNotification.Channel.BOTH;
 @Column(nullable=false) private boolean active=true;
 @Column(nullable=false,length=12) private String locale="en";
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @PrePersist void create(){createdAt=updatedAt=Instant.now();}
 @PreUpdate void update(){updatedAt=Instant.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getCode(){return code;} public void setCode(String v){code=v;} public String getTitleTemplate(){return titleTemplate;} public void setTitleTemplate(String v){titleTemplate=v;} public String getMessageTemplate(){return messageTemplate;} public void setMessageTemplate(String v){messageTemplate=v;} public String getShortMessageTemplate(){return shortMessageTemplate;} public void setShortMessageTemplate(String v){shortMessageTemplate=v;} public CustomerNotification.Category getCategory(){return category;} public void setCategory(CustomerNotification.Category v){category=v;} public CustomerNotification.Severity getSeverity(){return severity;} public void setSeverity(CustomerNotification.Severity v){severity=v;} public CustomerNotification.Channel getDefaultChannel(){return defaultChannel;} public void setDefaultChannel(CustomerNotification.Channel v){defaultChannel=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public String getLocale(){return locale;} public void setLocale(String v){locale=v;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}