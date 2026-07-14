package com.trademaster.ims.mobile.notifications.controller;

import com.trademaster.ims.mobile.auth.security.CustomerAuthentication;
import com.trademaster.ims.mobile.auth.service.CustomerAuthException;
import com.trademaster.ims.mobile.notifications.dto.NotificationDtos.*;
import com.trademaster.ims.mobile.notifications.service.CustomerNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/mobile/v1/notifications")
public class CustomerNotificationController{
 private final CustomerNotificationService service;
 public CustomerNotificationController(CustomerNotificationService s){service=s;}
 @GetMapping public NotificationPage list(Authentication a,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,@RequestParam(required=false) String category,@RequestParam(required=false) String readStatus){return service.list(id(a),page,size,category,readStatus);}
 @GetMapping("/unread-count") public UnreadCountResponse unread(Authentication a){return service.unread(id(a));}
 @GetMapping("/{number}") public NotificationDetail detail(Authentication a,@PathVariable String number){return service.detail(id(a),number);}
 @PostMapping("/{number}/read") public NotificationDetail read(Authentication a,@PathVariable String number){return service.markRead(id(a),number);}
 @PostMapping("/read-all") public UnreadCountResponse readAll(Authentication a){return service.markAllRead(id(a));}
 @GetMapping("/preferences") public List<NotificationPreferenceResponse> prefs(Authentication a){return service.getPreferences(id(a));}
 @PutMapping("/preferences") public List<NotificationPreferenceResponse> prefs(Authentication a,@RequestBody PreferenceUpdateRequest request){return service.updatePreferences(id(a),request);}
 private long id(Authentication a){if(a instanceof CustomerAuthentication c)return c.accountId();throw new CustomerAuthException(HttpStatus.UNAUTHORIZED,"AUTHENTICATION_REQUIRED","Authentication is required.");}
}