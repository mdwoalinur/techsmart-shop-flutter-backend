package com.trademaster.ims.mobile.notifications.model;

import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="customer_notifications", indexes={@Index(name="idx_cust_notif_customer_created",columnList="customer_account_id,created_at"),@Index(name="idx_cust_notif_customer_read",columnList="customer_account_id,read_status"),@Index(name="idx_cust_notif_customer_category",columnList="customer_account_id,category"),@Index(name="idx_cust_notif_number",columnList="notification_number",unique=true),@Index(name="idx_cust_notif_event_key",columnList="event_key",unique=true)})
public class CustomerNotification {
 public enum NotificationType { ORDER_CREATED, ORDER_CONFIRMED, ORDER_PROCESSING, ORDER_PACKED, ORDER_SHIPPED, ORDER_OUT_FOR_DELIVERY, ORDER_DELIVERED, ORDER_CANCEL_REQUESTED, ORDER_CANCEL_APPROVED, ORDER_CANCEL_REJECTED, RETURN_REQUESTED, RETURN_APPROVED, RETURN_REJECTED, PAYMENT_INITIATED, PAYMENT_PAID, PAYMENT_FAILED, PAYMENT_REVIEW_REQUIRED, PAYMENT_REJECTED, COD_PENDING, COD_PAYMENT_COLLECTED, COD_PAYMENT_RECONCILED, WALLET_PAYMENT_SUCCESS, MANUAL_PAYMENT_SUBMITTED, SYSTEM_MESSAGE, SECURITY_ALERT, SUPPORT_TICKET_CREATED, SUPPORT_TICKET_UPDATED, SUPPORT_TICKET_CLOSED, REVIEW_SUBMITTED, REVIEW_APPROVED, REVIEW_REJECTED }
 public enum Category { ORDER, PAYMENT, RETURN, CANCELLATION, ACCOUNT, SYSTEM, SUPPORT, REVIEW }
 public enum Severity { INFO, SUCCESS, WARNING, ERROR }
 public enum ReadStatus { UNREAD, READ }
 public enum Channel { IN_APP, EMAIL, BOTH }
 public enum DeliveryStatus { CREATED, IN_APP_DELIVERED, EMAIL_QUEUED, EMAIL_SENT, EMAIL_FAILED, EMAIL_DISABLED, SUPPRESSED_BY_PREFERENCE }
 public enum ActionType { OPEN_ORDER, OPEN_PAYMENT, OPEN_RETURN_REQUEST, OPEN_CANCELLATION_REQUEST, OPEN_PROFILE, OPEN_SUPPORT_TICKET, OPEN_REVIEW, NONE }
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="notification_number",nullable=false,unique=true,length=48) private String notificationNumber;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="customer_account_id",nullable=false) private CustomerAccount customer;
 @Column(name="event_key",nullable=false,unique=true,length=180) private String eventKey;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=40) private NotificationType type;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=24) private Category category;
 @Column(nullable=false,length=160) private String title;
 @Column(nullable=false,columnDefinition="TEXT") private String message;
 @Column(name="short_message",nullable=false,length=240) private String shortMessage;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private Severity severity;
 @Enumerated(EnumType.STRING) @Column(name="read_status",nullable=false,length=16) private ReadStatus readStatus=ReadStatus.UNREAD;
 @Column(name="read_at") private Instant readAt;
 @Enumerated(EnumType.STRING) @Column(name="delivery_status",nullable=false,length=32) private DeliveryStatus deliveryStatus=DeliveryStatus.CREATED;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=16) private Channel channel=Channel.IN_APP;
 @Column(name="related_entity_type",length=48) private String relatedEntityType;
 @Column(name="related_entity_reference",length=80) private String relatedEntityReference;
 @Enumerated(EnumType.STRING) @Column(name="action_type",length=40) private ActionType actionType=ActionType.NONE;
 @Column(name="action_reference",length=80) private String actionReference;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
 @Column(name="updated_at",nullable=false) private Instant updatedAt;
 @Column(name="expires_at") private Instant expiresAt;
 @Version private Long version;
 @PrePersist void create(){createdAt=updatedAt=Instant.now();}
 @PreUpdate void update(){updatedAt=Instant.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getNotificationNumber(){return notificationNumber;} public void setNotificationNumber(String v){notificationNumber=v;} public CustomerAccount getCustomer(){return customer;} public void setCustomer(CustomerAccount v){customer=v;} public String getEventKey(){return eventKey;} public void setEventKey(String v){eventKey=v;}
 public NotificationType getType(){return type;} public void setType(NotificationType v){type=v;} public Category getCategory(){return category;} public void setCategory(Category v){category=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public String getShortMessage(){return shortMessage;} public void setShortMessage(String v){shortMessage=v;} public Severity getSeverity(){return severity;} public void setSeverity(Severity v){severity=v;}
 public ReadStatus getReadStatus(){return readStatus;} public void setReadStatus(ReadStatus v){readStatus=v;} public Instant getReadAt(){return readAt;} public void setReadAt(Instant v){readAt=v;} public DeliveryStatus getDeliveryStatus(){return deliveryStatus;} public void setDeliveryStatus(DeliveryStatus v){deliveryStatus=v;} public Channel getChannel(){return channel;} public void setChannel(Channel v){channel=v;}
 public String getRelatedEntityType(){return relatedEntityType;} public void setRelatedEntityType(String v){relatedEntityType=v;} public String getRelatedEntityReference(){return relatedEntityReference;} public void setRelatedEntityReference(String v){relatedEntityReference=v;} public ActionType getActionType(){return actionType;} public void setActionType(ActionType v){actionType=v;} public String getActionReference(){return actionReference;} public void setActionReference(String v){actionReference=v;}
 public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;} public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;} public Long getVersion(){return version;}
}