package com.trademaster.ims.mobile.auth.security;

import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import com.trademaster.ims.mobile.auth.repository.CustomerAccountRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class CustomerAuthTokenFilter extends OncePerRequestFilter {
 private final CustomerJwtService jwt; private final CustomerAccountRepository accounts;
 public CustomerAuthTokenFilter(CustomerJwtService jwt,CustomerAccountRepository accounts){this.jwt=jwt;this.accounts=accounts;}
 @Override protected boolean shouldNotFilter(HttpServletRequest r){return !r.getRequestURI().startsWith("/api/mobile/v1/");}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader("Authorization");
  Authentication current=SecurityContextHolder.getContext().getAuthentication();
  if((current==null||current instanceof AnonymousAuthenticationToken)&&StringUtils.hasText(h)&&h.startsWith("Bearer ")){
   try{Long id=jwt.parseCustomerAccountId(h.substring(7));CustomerAccount a=accounts.findById(id).orElse(null);if(a!=null&&a.getStatus()==CustomerAccount.Status.ACTIVE&&a.isEmailVerified())SecurityContextHolder.getContext().setAuthentication(new CustomerAuthentication(id));}catch(JwtException|IllegalArgumentException ignored){}
  }
  chain.doFilter(req,res);
 }
}
