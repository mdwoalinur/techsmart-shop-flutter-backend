package com.trademaster.ims.mobile.notifications.repository;

import com.trademaster.ims.mobile.notifications.model.CustomerNotification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.*;

public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification,Long>, JpaSpecificationExecutor<CustomerNotification> {
 boolean existsByEventKey(String eventKey);
 Optional<CustomerNotification> findByEventKey(String eventKey);
 boolean existsByNotificationNumber(String notificationNumber);
 @Query("select n from CustomerNotification n where n.notificationNumber=:number and n.customer.id=:accountId") Optional<CustomerNotification> findOwned(@Param("accountId")Long accountId,@Param("number")String number);
 @Query("select count(n) from CustomerNotification n where n.customer.id=:accountId and n.readStatus=com.trademaster.ims.mobile.notifications.model.CustomerNotification$ReadStatus.UNREAD and (n.expiresAt is null or n.expiresAt>:now)") long unreadCount(@Param("accountId")Long accountId,@Param("now")Instant now);
 @Modifying @Query("update CustomerNotification n set n.readStatus=com.trademaster.ims.mobile.notifications.model.CustomerNotification$ReadStatus.READ,n.readAt=:now,n.updatedAt=:now where n.customer.id=:accountId and n.readStatus=com.trademaster.ims.mobile.notifications.model.CustomerNotification$ReadStatus.UNREAD") int markAllRead(@Param("accountId")Long accountId,@Param("now")Instant now);
}