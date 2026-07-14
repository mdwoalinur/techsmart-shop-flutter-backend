package com.trademaster.ims.mobile.auth;

import com.trademaster.ims.config.SecurityConfig;
import com.trademaster.ims.mobile.auth.controller.CustomerAuthController;
import com.trademaster.ims.mobile.auth.dto.CustomerAuthDtos.*;
import com.trademaster.ims.mobile.auth.security.*;
import com.trademaster.ims.mobile.auth.service.CustomerAuthService;
import com.trademaster.ims.mobile.common.exception.MobileExceptionHandler;
import com.trademaster.ims.security.UserDetailsServiceImpl;
import com.trademaster.ims.security.JwtUtils;
import jakarta.servlet.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerAuthController.class) @Import({SecurityConfig.class,MobileExceptionHandler.class})
@ContextConfiguration(classes={CustomerAuthController.class,SecurityConfig.class,MobileExceptionHandler.class})
class CustomerAuthSecurityTest {
 @Autowired MockMvc mvc; @MockitoBean CustomerAuthService service; @MockitoBean UserDetailsServiceImpl users; @MockitoBean JwtUtils jwtUtils; @MockitoBean CustomerAuthTokenFilter customerFilter;
 @BeforeEach void pass()throws Exception{doAnswer(i->{((FilterChain)i.getArgument(2)).doFilter((ServletRequest)i.getArgument(0),(ServletResponse)i.getArgument(1));return null;}).when(customerFilter).doFilter(any(),any(),any());}
 @Test void emptyRegistrationUsesValidationEnvelope()throws Exception{mvc.perform(post("/api/mobile/v1/auth/register").contentType("application/json").content("{}" )).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));}
 @Test void loginEndpointIsPublic()throws Exception{when(service.login(any())).thenReturn(new AuthResponse("a","r",Instant.now(),Instant.now(),null));mvc.perform(post("/api/mobile/v1/auth/login").contentType("application/json").content("{\"email\":\"test@example.com\",\"password\":\"Strong1!\"}")).andExpect(status().isOk());}
 @Test void profileRequiresAuthentication()throws Exception{mvc.perform(get("/api/mobile/v1/auth/me")).andExpect(status().isUnauthorized());}
 @Test void employeeAuthorityCannotBecomeCustomer()throws Exception{mvc.perform(get("/api/mobile/v1/auth/me").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("employee").roles("EMPLOYEE"))).andExpect(status().isForbidden());}
 @Test void customerPrincipalCanReadSafeProfile()throws Exception{when(service.profile(7L)).thenReturn(new CustomerProfileResponse(1L,"C1","Test","t@example.com","01712345678","RETAIL",null,null,null,null,null,null,true));mvc.perform(get("/api/mobile/v1/auth/me").with(authentication(new CustomerAuthentication(7L)))).andExpect(status().isOk()).andExpect(jsonPath("$.data.email").value("t@example.com")).andExpect(jsonPath("$.data.passwordHash").doesNotExist()).andExpect(jsonPath("$.data.creditLimit").doesNotExist());}
 @Test void unsupportedAuthMethodIsDenied()throws Exception{mvc.perform(delete("/api/mobile/v1/auth/me")).andExpect(status().isUnauthorized());}
}


