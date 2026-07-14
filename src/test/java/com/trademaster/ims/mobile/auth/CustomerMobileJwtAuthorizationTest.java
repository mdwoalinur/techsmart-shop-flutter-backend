package com.trademaster.ims.mobile.auth;

import com.trademaster.ims.config.SecurityConfig;
import com.trademaster.ims.mobile.auth.model.CustomerAccount;
import com.trademaster.ims.mobile.auth.repository.CustomerAccountRepository;
import com.trademaster.ims.mobile.auth.security.CustomerAuthentication;
import com.trademaster.ims.mobile.auth.security.CustomerAuthTokenFilter;
import com.trademaster.ims.mobile.auth.security.CustomerJwtService;
import com.trademaster.ims.mobile.catalog.controller.MobileHealthController;
import com.trademaster.ims.mobile.checkout.controller.MobileCheckoutController;
import com.trademaster.ims.mobile.checkout.dto.CheckoutDtos.*;
import com.trademaster.ims.mobile.checkout.service.MobileCheckoutService;
import com.trademaster.ims.mobile.common.exception.MobileExceptionHandler;
import com.trademaster.ims.mobile.orders.controller.MobileOrderController;
import com.trademaster.ims.mobile.orders.dto.OrderDtos.*;
import com.trademaster.ims.mobile.orders.service.MobileOrderService;
import com.trademaster.ims.mobile.shopping.controller.CustomerShoppingController;
import com.trademaster.ims.mobile.shopping.dto.ShoppingDtos.*;
import com.trademaster.ims.mobile.shopping.service.CustomerShoppingService;
import com.trademaster.ims.security.JwtUtils;
import com.trademaster.ims.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers={CustomerShoppingController.class,MobileCheckoutController.class,MobileOrderController.class,MobileHealthController.class})
@Import({SecurityConfig.class,MobileExceptionHandler.class,CustomerAuthTokenFilter.class,CustomerJwtService.class})
@ContextConfiguration(classes={CustomerShoppingController.class,MobileCheckoutController.class,MobileOrderController.class,MobileHealthController.class,SecurityConfig.class,MobileExceptionHandler.class,CustomerAuthTokenFilter.class,CustomerJwtService.class})
@TestPropertySource(properties={"jwt.secret=0123456789012345678901234567890123456789012345678901234567890123","jwt.expiration=86400000","app.customer-auth.access-token-seconds=900"})
class CustomerMobileJwtAuthorizationTest{
 @Autowired MockMvc mvc; @Autowired CustomerJwtService customerJwt; @Autowired CustomerAuthTokenFilter customerFilter; @MockitoBean CustomerAccountRepository accounts; @MockitoBean CustomerShoppingService shopping; @MockitoBean MobileCheckoutService checkout; @MockitoBean MobileOrderService orders; @MockitoBean UserDetailsServiceImpl users; @MockitoBean JwtUtils employeeJwt;
 @BeforeEach void setup(){when(accounts.findById(7L)).thenReturn(Optional.of(active()));when(shopping.accountId(any())).thenAnswer(i->account(i.getArgument(0)));when(checkout.account(any())).thenAnswer(i->account(i.getArgument(0)));when(orders.account(any())).thenAnswer(i->account(i.getArgument(0)));when(shopping.getCart(eq(7L),any())).thenReturn(new CartResponse(1L,List.of(),0,0,BigDecimal.ZERO,List.of(),Instant.now()));when(shopping.getWishlist(eq(7L),any())).thenReturn(new WishlistResponse(1L,List.of(),0,List.of(),Instant.now()));when(checkout.listAddresses(7L)).thenReturn(List.of());when(checkout.review(eq(7L),any())).thenReturn(new ReviewResponse("rv",Instant.now().plusSeconds(60),"v",List.of(),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,"BDT",null,null,List.of(),List.of(),true));OrderSummary s=new OrderSummary("TSS-1",Instant.parse("2026-01-01T00:00:00Z"),"PENDING_CONFIRMATION","Pending Confirmation","NOT_STARTED",BigDecimal.TEN,"BDT",1,1,"Cable",null,0,"Standard",true,false);when(orders.list(eq(7L),anyInt(),anyInt(),any(),any(),any(),any(),any(),any())).thenReturn(new OrderPage(List.of(s),0,10,1,1,true,true));}
 @Test void customerJwtFilterResolvesRoleCustomer()throws Exception{SecurityContextHolder.clearContext();try{String token=bearer(customerJwt.issue(7L).token());MockHttpServletRequest req=new MockHttpServletRequest("GET","/api/mobile/v1/cart");req.addHeader("Authorization",token);customerFilter.doFilter(req,new MockHttpServletResponse(),new MockFilterChain());Authentication auth=SecurityContextHolder.getContext().getAuthentication();assertInstanceOf(CustomerAuthentication.class,auth);assertTrue(auth.getAuthorities().stream().anyMatch(x->"ROLE_CUSTOMER".equals(x.getAuthority())));}finally{SecurityContextHolder.clearContext();}}
 @Test void validCustomerAuthenticationAccessesCartWishlistAddressesCheckoutAndOrders()throws Exception{CustomerAuthentication auth=new CustomerAuthentication(7L);mvc.perform(get("/api/mobile/v1/cart").with(authentication(auth))).andExpect(status().isOk()).andExpect(jsonPath("$.cartId").value(1));mvc.perform(get("/api/mobile/v1/wishlist").with(authentication(auth))).andExpect(status().isOk());mvc.perform(get("/api/mobile/v1/addresses").with(authentication(auth))).andExpect(status().isOk());mvc.perform(post("/api/mobile/v1/checkout/review").with(authentication(auth)).contentType("application/json").content("{\"addressId\":1,\"deliveryMethodId\":1}")).andExpect(status().isOk());mvc.perform(get("/api/mobile/v1/orders").with(authentication(auth))).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].orderNumber").value("TSS-1"));}
 @Test void missingExpiredAndInvalidCustomerTokenReturn401()throws Exception{mvc.perform(get("/api/mobile/v1/cart")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));ReflectionTestUtils.setField(customerJwt,"accessSeconds",-1L);String expired=bearer(customerJwt.issue(7L).token());ReflectionTestUtils.setField(customerJwt,"accessSeconds",900L);mvc.perform(get("/api/mobile/v1/cart").header("Authorization",expired)).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));mvc.perform(get("/api/mobile/v1/cart").header("Authorization",bearer(wrongSecretToken()))).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message").value("Authentication is required."));}
 @Test void employeeAuthenticationOnCustomerEndpointReturns403()throws Exception{mvc.perform(get("/api/mobile/v1/cart").with(user("employee").roles("EMPLOYEE"))).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("ACCESS_DENIED"));}
 @Test void publicMobileHealthRemainsOpen()throws Exception{mvc.perform(get("/api/mobile/v1/health")).andExpect(status().isOk()).andExpect(content().string(containsString("TechSmart Shop Mobile API")));}
 private long account(Authentication a){assertInstanceOf(CustomerAuthentication.class,a);assertTrue(a.getAuthorities().stream().anyMatch(x->"ROLE_CUSTOMER".equals(x.getAuthority())));return ((CustomerAuthentication)a).accountId();}
 private CustomerAccount active(){CustomerAccount a=new CustomerAccount();a.setId(7L);a.setEmail("customer@example.test");a.setStatus(CustomerAccount.Status.ACTIVE);a.setEmailVerified(true);return a;}
 private String bearer(String token){return "Bearer "+token;}
 private String wrongSecretToken(){Instant now=Instant.now();return Jwts.builder().setSubject("7").setAudience("techsmart-customer").claim("type","customer_access").claim("authority","ROLE_CUSTOMER").setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusSeconds(900))).signWith(Keys.hmacShaKeyFor("9999999999999999999999999999999999999999999999999999999999999999".getBytes(StandardCharsets.UTF_8)),SignatureAlgorithm.HS512).compact();}
}
