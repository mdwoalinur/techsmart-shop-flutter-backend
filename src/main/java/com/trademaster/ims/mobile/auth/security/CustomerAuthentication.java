package com.trademaster.ims.mobile.auth.security;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
public final class CustomerAuthentication extends AbstractAuthenticationToken{
 private final Long accountId;
 public CustomerAuthentication(Long accountId){super(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));this.accountId=accountId;setAuthenticated(true);}
 public Long accountId(){return accountId;} @Override public Object getCredentials(){return "";} @Override public Object getPrincipal(){return accountId;}
}
