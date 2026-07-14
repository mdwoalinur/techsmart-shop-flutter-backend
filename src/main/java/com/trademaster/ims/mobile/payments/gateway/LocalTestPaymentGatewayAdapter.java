package com.trademaster.ims.mobile.payments.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class LocalTestPaymentGatewayAdapter implements PaymentGatewayAdapter{
 private final ObjectMapper mapper=new ObjectMapper(); private final Environment env; private final boolean enabled; private final String secret;
 public LocalTestPaymentGatewayAdapter(Environment env,@Value("${techsmart.payments.local-gateway.enabled:${TECHSMART_LOCAL_GATEWAY_ENABLED:true}}")boolean enabled,@Value("${techsmart.payments.local-gateway.secret:${TECHSMART_LOCAL_GATEWAY_SECRET:local-test-gateway-secret-change-me}}")String secret){this.env=env;this.enabled=enabled;this.secret=secret;}
 public boolean available(){return enabled&&!List.of(env.getActiveProfiles()).contains("prod");}
 @Override public boolean supports(String providerCode){return available()&&"LOCAL_TEST".equalsIgnoreCase(providerCode);}
 @Override public GatewaySession createSession(GatewaySessionRequest r){if(!supports("LOCAL_TEST"))throw new IllegalStateException("Local test gateway is disabled");String sid="LTG-"+r.paymentNumber()+"-"+r.attemptNumber()+"-"+UUID.randomUUID().toString().substring(0,8);return new GatewaySession("LOCAL_TEST",sid,"techsmart://payments/local-test?sessionId="+sid,Instant.now().plus(15,ChronoUnit.MINUTES));}
 @Override public VerificationResult verifyCallback(String rawBody,String signature){try{boolean valid=constant(signature,sign(rawBody));Map<String,Object> m=mapper.readValue(rawBody,new TypeReference<Map<String,Object>>(){});return new VerificationResult(valid,str(m,"eventId"),str(m,"eventType","payment.succeeded"),str(m,"paymentNumber"),str(m,"orderNumber"),num(m,"attemptNumber"),str(m,"transactionId"),money(m,"amount"),str(m,"currency","BDT"),str(m,"status","SUCCEEDED"),valid?null:"Invalid local test gateway signature",Map.of("provider","LOCAL_TEST"));}catch(Exception e){return new VerificationResult(false,null,"invalid",null,null,0,null,BigDecimal.ZERO,"BDT","FAILED","Invalid webhook payload",Map.of());}}
 @Override public TransactionQueryResult queryTransaction(String paymentNumber){return new TransactionQueryResult("LOCAL_TEST",paymentNumber,null,BigDecimal.ZERO,"BDT","PENDING");}
 public String sign(String body){try{Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));return "sha256="+HexFormat.of().formatHex(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}}
 private boolean constant(String a,String b){if(a==null||b==null)return false;return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8),b.getBytes(StandardCharsets.UTF_8));}
 private String str(Map<String,Object>m,String k){return str(m,k,null);}private String str(Map<String,Object>m,String k,String d){Object v=m.get(k);return v==null?d:v.toString();}private int num(Map<String,Object>m,String k){Object v=m.get(k);return v==null?0:Integer.parseInt(v.toString());}private BigDecimal money(Map<String,Object>m,String k){Object v=m.get(k);return v==null?BigDecimal.ZERO:new BigDecimal(v.toString());}
}

