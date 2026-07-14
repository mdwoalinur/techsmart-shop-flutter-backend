package com.trademaster.ims.mobile.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.*;
import java.util.*;

@Component
public class CustomerJwtService {
 @Value("${jwt.secret}") private String secret;
 @Value("${app.customer-auth.access-token-seconds:900}") private long accessSeconds;
 private Key key(){return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
 public IssuedAccess issue(Long accountId){Instant now=Instant.now(), expiry=now.plusSeconds(accessSeconds);String token=Jwts.builder().setSubject(accountId.toString()).setAudience("techsmart-customer").claim("type","customer_access").claim("authority","ROLE_CUSTOMER").setId(UUID.randomUUID().toString()).setIssuedAt(Date.from(now)).setExpiration(Date.from(expiry)).signWith(key(),SignatureAlgorithm.HS512).compact();return new IssuedAccess(token,expiry);}
 public Long parseCustomerAccountId(String token){Claims c=Jwts.parserBuilder().setSigningKey(key()).requireAudience("techsmart-customer").build().parseClaimsJws(token).getBody();if(!"customer_access".equals(c.get("type",String.class))||!"ROLE_CUSTOMER".equals(c.get("authority",String.class)))throw new JwtException("Invalid customer token type");return Long.valueOf(c.getSubject());}
 public record IssuedAccess(String token,Instant expiresAt){}
}
