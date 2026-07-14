package com.trademaster.ims.mobile.notifications;

import com.trademaster.ims.config.SecurityConfig;
import com.trademaster.ims.mobile.auth.security.*;
import com.trademaster.ims.mobile.common.exception.MobileExceptionHandler;
import com.trademaster.ims.mobile.notifications.controller.CustomerNotificationController;
import com.trademaster.ims.mobile.notifications.dto.NotificationDtos.*;
import com.trademaster.ims.mobile.notifications.service.CustomerNotificationService;
import com.trademaster.ims.security.*;
import jakarta.servlet.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerNotificationController.class)
@Import({SecurityConfig.class,MobileExceptionHandler.class})
@ContextConfiguration(classes={CustomerNotificationController.class,SecurityConfig.class,MobileExceptionHandler.class})
class CustomerNotificationSecurityTest{
 @Autowired MockMvc mvc; @MockitoBean CustomerNotificationService service; @MockitoBean UserDetailsServiceImpl users; @MockitoBean JwtUtils jwt; @MockitoBean CustomerAuthTokenFilter filter;
 @BeforeEach void setup()throws Exception{doAnswer(i->{((FilterChain)i.getArgument(2)).doFilter((ServletRequest)i.getArgument(0),(ServletResponse)i.getArgument(1));return null;}).when(filter).doFilter(any(),any(),any());NotificationSummary s=new NotificationSummary("NTF-1","ORDER_CREATED","ORDER","Order received","Order TSS-1 received","INFO",false,Instant.parse("2026-01-01T00:00:00Z"),"ORDER","TSS-1","OPEN_ORDER");when(service.list(eq(7L),anyInt(),anyInt(),any(),any())).thenReturn(new NotificationPage(List.of(s),0,20,1,1,true,true));when(service.unread(7L)).thenReturn(new UnreadCountResponse(1));when(service.detail(7L,"NTF-1")).thenReturn(new NotificationDetail("NTF-1","ORDER_CREATED","ORDER","Order received","Message","INFO",false,Instant.parse("2026-01-01T00:00:00Z"),null,"ORDER","TSS-1","OPEN_ORDER","TSS-1"));when(service.markRead(7L,"NTF-1")).thenReturn(new NotificationDetail("NTF-1","ORDER_CREATED","ORDER","Order received","Message","INFO",true,Instant.parse("2026-01-01T00:00:00Z"),Instant.parse("2026-01-01T00:01:00Z"),"ORDER","TSS-1","OPEN_ORDER","TSS-1"));when(service.markAllRead(7L)).thenReturn(new UnreadCountResponse(0));when(service.getPreferences(7L)).thenReturn(List.of(new NotificationPreferenceResponse("ORDER",true,true,false)));when(service.updatePreferences(eq(7L),any())).thenReturn(List.of(new NotificationPreferenceResponse("ORDER",false,false,false)));}
 @Test void anonymousDenied()throws Exception{mvc.perform(get("/api/mobile/v1/notifications")).andExpect(status().isUnauthorized());}
 @Test void employeeDenied()throws Exception{mvc.perform(get("/api/mobile/v1/notifications").with(user("employee").roles("EMPLOYEE"))).andExpect(status().isForbidden());}
 @Test void customerListsOwnNotificationsOnly()throws Exception{mvc.perform(get("/api/mobile/v1/notifications").with(authentication(new CustomerAuthentication(7L)))).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].notificationNumber").value("NTF-1"));verify(service).list(eq(7L),eq(0),eq(20),any(),any());}
 @Test void detailAndMarkReadUsePrincipal()throws Exception{mvc.perform(get("/api/mobile/v1/notifications/NTF-1").with(authentication(new CustomerAuthentication(7L)))).andExpect(status().isOk()).andExpect(jsonPath("$.actionReference").value("TSS-1"));mvc.perform(post("/api/mobile/v1/notifications/NTF-1/read").with(authentication(new CustomerAuthentication(7L)))).andExpect(status().isOk()).andExpect(jsonPath("$.read").value(true));verify(service).detail(7L,"NTF-1");verify(service).markRead(7L,"NTF-1");}
 @Test void unreadReadAllAndPreferencesUsePrincipal()throws Exception{mvc.perform(get("/api/mobile/v1/notifications/unread-count").with(authentication(new CustomerAuthentication(7L)))).andExpect(status().isOk()).andExpect(jsonPath("$.unreadCount").value(1));mvc.perform(post("/api/mobile/v1/notifications/read-all").with(authentication(new CustomerAuthentication(7L)))).andExpect(status().isOk()).andExpect(jsonPath("$.unreadCount").value(0));mvc.perform(get("/api/mobile/v1/notifications/preferences").with(authentication(new CustomerAuthentication(7L)))).andExpect(status().isOk()).andExpect(jsonPath("$[0].category").value("ORDER"));mvc.perform(put("/api/mobile/v1/notifications/preferences").with(authentication(new CustomerAuthentication(7L))).contentType("application/json").content("{\"preferences\":[{\"category\":\"ORDER\",\"inAppEnabled\":false,\"emailEnabled\":false,\"customerId\":999}]}")).andExpect(status().isOk()).andExpect(jsonPath("$[0].inAppEnabled").value(false));verify(service).updatePreferences(eq(7L),any());}
}