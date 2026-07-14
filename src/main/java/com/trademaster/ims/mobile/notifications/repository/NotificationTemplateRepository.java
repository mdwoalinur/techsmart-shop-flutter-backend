package com.trademaster.ims.mobile.notifications.repository;

import com.trademaster.ims.mobile.notifications.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate,Long> {
 Optional<NotificationTemplate> findByCodeAndLocaleAndActiveTrue(String code,String locale);
 boolean existsByCodeAndLocale(String code,String locale);
}