package com.trademaster.ims.mobile.auth.service;

import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomerAuthMailService {
 private final JavaMailSender sender;
 @Value("${app.email.enabled:false}") private boolean enabled;
 @Value("${app.email.from:${spring.mail.username:}}") private String from;
 public CustomerAuthMailService(JavaMailSender sender){this.sender=sender;}
 public void registrationOtp(CustomerAccount a,String name,String otp,long minutes){otp(a,name,otp,minutes,"Verify your TechSmart Shop email","registration");}
 public void passwordResetOtp(CustomerAccount a,String name,String otp,long minutes){otp(a,name,otp,minutes,"Reset your TechSmart Shop password","password reset");}
 public void passwordChanged(CustomerAccount a,String name){send(a.getEmail(),"Your TechSmart Shop password was changed",html(name,"Your password has been changed. If you did not do this, contact support immediately."));}
 private void otp(CustomerAccount a,String name,String otp,long minutes,String subject,String purpose){send(a.getEmail(),subject,html(name,"Your six-digit "+purpose+" code is <strong style='font-size:24px;letter-spacing:4px'>"+otp+"</strong>. It expires in "+minutes+" minutes. Never share this code."));}
 private String html(String name,String body){return "<html><body style='font-family:Arial;color:#102033'><h2>TechSmart Shop</h2><p>Hello "+escape(name)+",</p><p>"+body+"</p><p>If you did not request this, ignore this email and keep your account secure.</p></body></html>";}
 private void send(String to,String subject,String body){if(!enabled||!StringUtils.hasText(from))throw new CustomerAuthException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,"MAIL_DELIVERY_FAILED","Verification email is temporarily unavailable.");try{MimeMessage m=sender.createMimeMessage();MimeMessageHelper h=new MimeMessageHelper(m,false,"UTF-8");h.setFrom(from.trim());h.setTo(to);h.setSubject(subject);h.setText(body,true);sender.send(m);}catch(MailException|jakarta.mail.MessagingException e){throw new CustomerAuthException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,"MAIL_DELIVERY_FAILED","Verification email could not be delivered. Please try again later.");}}
 private String escape(String s){return s==null?"Customer":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");}
}
