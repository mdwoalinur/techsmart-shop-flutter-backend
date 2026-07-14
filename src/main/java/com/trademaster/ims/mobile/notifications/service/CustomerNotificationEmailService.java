package com.trademaster.ims.mobile.notifications.service;

import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import com.trademaster.ims.mobile.notifications.model.CustomerNotification;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomerNotificationEmailService {
 private static final Logger log=LoggerFactory.getLogger(CustomerNotificationEmailService.class);
 private final JavaMailSender sender;
 @Value("${app.email.enabled:false}") private boolean enabled;
 @Value("${app.email.from:${spring.mail.username:}}") private String from;
 public CustomerNotificationEmailService(JavaMailSender sender){this.sender=sender;}
 public CustomerNotification.DeliveryStatus send(CustomerAccount account,CustomerNotification n){
  if(!enabled||!StringUtils.hasText(from)||account==null||!StringUtils.hasText(account.getEmail()))return CustomerNotification.DeliveryStatus.EMAIL_DISABLED;
  try{MimeMessage m=sender.createMimeMessage();MimeMessageHelper h=new MimeMessageHelper(m,false,"UTF-8");h.setFrom(from.trim());h.setTo(account.getEmail().trim());h.setSubject("TechSmart Shop - "+n.getTitle());h.setText(html(account,n),true);sender.send(m);return CustomerNotification.DeliveryStatus.EMAIL_SENT;}catch(Exception ex){log.warn("Customer notification email failed. notificationNumber={}, accountId={}",n.getNotificationNumber(),account.getId());return CustomerNotification.DeliveryStatus.EMAIL_FAILED;}
 }
 private String html(CustomerAccount a,CustomerNotification n){String name=a.getCustomer()==null?"Customer":a.getCustomer().getCustomerName();return "<html><body style='font-family:Arial;color:#102033'><h2>TechSmart Shop</h2><p>Hello "+esc(name)+",</p><h3>"+esc(n.getTitle())+"</h3><p>"+esc(n.getMessage())+"</p><p>Open the TechSmart Shop app to view details. This email never includes passwords, OTPs, tokens, or internal IDs.</p></body></html>";}
 private String esc(String s){return s==null?"":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");}
}