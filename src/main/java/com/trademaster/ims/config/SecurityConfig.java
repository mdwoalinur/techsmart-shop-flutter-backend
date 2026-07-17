package com.trademaster.ims.config;

import com.trademaster.ims.mobile.auth.security.CustomerAuthTokenFilter;
import com.trademaster.ims.security.AuthTokenFilter;
import com.trademaster.ims.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Configuration @EnableWebSecurity @EnableMethodSecurity(prePostEnabled=true)
public class SecurityConfig {
 private final UserDetailsServiceImpl users; private final CustomerAuthTokenFilter customerFilter;
 public SecurityConfig(UserDetailsServiceImpl users,CustomerAuthTokenFilter customerFilter){this.users=users;this.customerFilter=customerFilter;}
 @Bean public AuthTokenFilter authenticationJwtTokenFilter(){return new AuthTokenFilter();}
 @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
 @Bean public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean public CorsConfigurationSource corsConfigurationSource(){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:4200"));c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("*"));c.setAllowCredentials(true);UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
 @Bean public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
  http.cors(c->c.configurationSource(corsConfigurationSource())).csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).exceptionHandling(e->e
   .authenticationEntryPoint((req,res,ex)->security(res,HttpStatus.UNAUTHORIZED,"AUTHENTICATION_REQUIRED","Authentication is required.",req.getRequestURI()))
   .accessDeniedHandler((req,res,ex)->{Authentication a=SecurityContextHolder.getContext().getAuthentication();boolean missing=a==null||a instanceof AnonymousAuthenticationToken||!a.isAuthenticated();security(res,missing?HttpStatus.UNAUTHORIZED:HttpStatus.FORBIDDEN,missing?"AUTHENTICATION_REQUIRED":"ACCESS_DENIED",missing?"Authentication is required.":"You do not have permission to perform this action.",req.getRequestURI());})
  ).authorizeHttpRequests(a->a
   .requestMatchers(HttpMethod.POST,"/api/dev/phase11-fixtures/customer-fulfillment","/api/dev/phase11-fixtures/cleanup").permitAll()
   .requestMatchers(HttpMethod.GET,"/api/dev/phase11-fixtures/customer-fulfillment/*/verification").permitAll()
   .requestMatchers(HttpMethod.GET,"/api/mobile/v1/health","/api/mobile/v1/categories","/api/mobile/v1/categories/*","/api/mobile/v1/categories/*/products","/api/mobile/v1/products","/api/mobile/v1/products/*","/api/mobile/v1/products/search","/api/mobile/v1/offers","/api/mobile/v1/offers/*","/api/mobile/v1/offers/*/products","/api/mobile/v1/products/*/reviews","/api/mobile/v1/products/*/review-summary","/api/mobile/v1/help/faqs","/api/mobile/v1/help/faqs/*").permitAll()
   .requestMatchers(HttpMethod.POST,"/api/mobile/v1/auth/register","/api/mobile/v1/auth/verify-registration","/api/mobile/v1/auth/resend-registration-otp","/api/mobile/v1/auth/login","/api/mobile/v1/auth/refresh","/api/mobile/v1/auth/forgot-password","/api/mobile/v1/auth/verify-password-reset-otp","/api/mobile/v1/auth/reset-password").permitAll()
   .requestMatchers(HttpMethod.GET,"/api/mobile/v1/auth/me").hasRole("CUSTOMER")
   .requestMatchers(HttpMethod.PUT,"/api/mobile/v1/auth/me").hasRole("CUSTOMER")
   .requestMatchers(HttpMethod.POST,"/api/mobile/v1/auth/logout","/api/mobile/v1/auth/change-password","/api/mobile/v1/profile/photo","/api/mobile/v1/products/*/reviews").hasRole("CUSTOMER")
   .requestMatchers("/api/mobile/v1/cart","/api/mobile/v1/cart/**","/api/mobile/v1/wishlist","/api/mobile/v1/wishlist/**").hasRole("CUSTOMER")
   .requestMatchers("/api/mobile/v1/admin/reviews/**").hasAnyRole("ADMIN","SUPER_ADMIN")
   .requestMatchers(HttpMethod.GET,"/api/mobile/v1/delivery-methods").permitAll()
   .requestMatchers(HttpMethod.GET,"/api/mobile/v1/orders/*/reviewable-items").hasRole("CUSTOMER")
   .requestMatchers("/api/mobile/v1/addresses","/api/mobile/v1/addresses/**","/api/mobile/v1/checkout/**","/api/mobile/v1/orders","/api/mobile/v1/orders/**","/api/mobile/v1/notifications","/api/mobile/v1/notifications/**","/api/mobile/v1/payments/mobile-wallet/**","/api/mobile/v1/support/**","/api/mobile/v1/reviews/**").hasRole("CUSTOMER")
   .requestMatchers(HttpMethod.POST,"/api/mobile/v1/payments/webhooks/*").permitAll()
   .requestMatchers("/api/orders/fulfillment","/api/orders/fulfillment/**").hasAnyRole("ADMIN","SUPER_ADMIN","MANAGER")
   .requestMatchers("/api/mobile/v1/**").denyAll()
   .requestMatchers(HttpMethod.POST,"/api/auth/login","/api/auth/forgot-password/request-otp","/api/auth/forgot-password/resend-otp","/api/auth/forgot-password/verify-otp","/api/auth/forgot-password/reset").permitAll()
   .requestMatchers(HttpMethod.GET,"/api/files/profiles/**","/uploads/products/**","/uploads/customers/**","/uploads/suppliers/**","/uploads/product-variations/**").permitAll()
   .anyRequest().authenticated()).userDetailsService(users)
   .addFilterBefore(customerFilter,UsernamePasswordAuthenticationFilter.class)
   .addFilterBefore(authenticationJwtTokenFilter(),UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
 private void security(HttpServletResponse res,HttpStatus status,String code,String message,String path)throws IOException{res.setStatus(status.value());res.setContentType(MediaType.APPLICATION_JSON_VALUE);res.getWriter().write("{\"success\":false,\"code\":\""+esc(code)+"\",\"message\":\""+esc(message)+"\",\"fieldErrors\":[],\"timestamp\":\""+Instant.now()+"\",\"path\":\""+esc(path)+"\",\"traceId\":null}");}
 private String esc(String v){return v==null?"":v.replace("\\","\\\\").replace("\"","\\\"");}
}


