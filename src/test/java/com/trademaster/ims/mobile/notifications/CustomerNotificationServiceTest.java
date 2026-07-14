package com.trademaster.ims.mobile.notifications;

import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import com.trademaster.ims.mobile.auth.repository.CustomerAccountRepository;
import com.trademaster.ims.mobile.notifications.model.*;
import com.trademaster.ims.mobile.notifications.repository.*;
import com.trademaster.ims.mobile.notifications.service.*;
import com.trademaster.ims.model.Customer;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CustomerNotificationServiceTest{
 @Mock CustomerNotificationRepository notifications; @Mock NotificationTemplateRepository templates; @Mock CustomerNotificationPreferenceRepository preferences; @Mock CustomerAccountRepository accounts; @Mock CustomerNotificationEmailService email; CustomerNotificationService service; CustomerAccount account;
 @BeforeEach void setup(){MockitoAnnotations.openMocks(this);service=new CustomerNotificationService(notifications,templates,preferences,accounts,email);account=new CustomerAccount();account.setId(7L);account.setEmail("customer@example.com");Customer c=new Customer();c.setCustomerName("Customer");account.setCustomer(c);lenient().when(accounts.findById(7L)).thenReturn(Optional.of(account));lenient().when(templates.existsByCodeAndLocale(anyString(),eq("en"))).thenReturn(true);lenient().when(templates.findByCodeAndLocaleAndActiveTrue(anyString(),eq("en"))).thenAnswer(i->{NotificationTemplate t=new NotificationTemplate();t.setCode(i.getArgument(0));t.setTitleTemplate("Title {orderNumber} {paymentNumber}");t.setMessageTemplate("Message {orderNumber} {paymentNumber} {amount} {method} {provider}");t.setShortMessageTemplate("Short {orderNumber} {paymentNumber}");String code=i.getArgument(0);t.setCategory(code.toString().startsWith("ORDER")?CustomerNotification.Category.ORDER:CustomerNotification.Category.PAYMENT);t.setSeverity(CustomerNotification.Severity.INFO);t.setDefaultChannel(CustomerNotification.Channel.BOTH);return Optional.of(t);});lenient().when(preferences.findByAccountAndCategory(anyLong(),any())).thenReturn(Optional.empty());lenient().when(preferences.save(any())).thenAnswer(i->i.getArgument(0));lenient().when(notifications.existsByNotificationNumber(anyString())).thenReturn(false);lenient().when(notifications.saveAndFlush(any())).thenAnswer(i->i.getArgument(0));lenient().when(notifications.save(any())).thenAnswer(i->i.getArgument(0));lenient().when(email.send(any(),any())).thenReturn(CustomerNotification.DeliveryStatus.EMAIL_DISABLED);}
 @Test void createsOrderNotificationFromTemplate(){CustomerNotification n=service.orderCreated(7L,"TSS-1",new BigDecimal("125.50"),"BDT");assertThat(n.getNotificationNumber()).startsWith("NTF-");assertThat(n.getRelatedEntityReference()).isEqualTo("TSS-1");assertThat(n.getActionType()).isEqualTo(CustomerNotification.ActionType.OPEN_ORDER);verify(notifications).saveAndFlush(any());}
 @Test void duplicateEventKeyReturnsExisting(){CustomerNotification existing=new CustomerNotification();existing.setNotificationNumber("NTF-OLD");when(notifications.existsByEventKey("ORDER_CREATED:TSS-1")).thenReturn(true);when(notifications.findByEventKey("ORDER_CREATED:TSS-1")).thenReturn(Optional.of(existing));CustomerNotification n=service.orderCreated(7L,"TSS-1",BigDecimal.ONE,"BDT");assertThat(n.getNotificationNumber()).isEqualTo("NTF-OLD");verify(notifications,never()).saveAndFlush(any());}
 @Test void emailDisabledDoesNotBlockInAppNotification(){CustomerNotification n=service.paymentPaid(7L,"TSS-1","TSP-1",new BigDecimal("125.50"),"BKASH");assertThat(n.getDeliveryStatus()).isEqualTo(CustomerNotification.DeliveryStatus.EMAIL_DISABLED);verify(email,atLeastOnce()).send(eq(account),any());}
}