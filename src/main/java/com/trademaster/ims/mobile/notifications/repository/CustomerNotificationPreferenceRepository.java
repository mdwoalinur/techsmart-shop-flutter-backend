package com.trademaster.ims.mobile.notifications.repository;

import com.trademaster.ims.mobile.notifications.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface CustomerNotificationPreferenceRepository extends JpaRepository<CustomerNotificationPreference,Long> {
 @Query("select p from CustomerNotificationPreference p where p.customer.id=:accountId order by p.category asc") List<CustomerNotificationPreference> findByAccount(@Param("accountId")Long accountId);
 @Query("select p from CustomerNotificationPreference p where p.customer.id=:accountId and p.category=:category") Optional<CustomerNotificationPreference> findByAccountAndCategory(@Param("accountId")Long accountId,@Param("category")CustomerNotification.Category category);
}